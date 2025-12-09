package fi.digitraffic.graphql.rail.queries;

import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;

import fi.digitraffic.graphql.rail.config.graphql.CustomException;
import fi.digitraffic.graphql.rail.entities.QTrain;
import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.querydsl.AllFields;
import fi.digitraffic.graphql.rail.repositories.TrainIdOptimizer;
import fi.digitraffic.graphql.rail.repositories.TrainRepository;
import fi.digitraffic.graphql.rail.to.TrainTOConverter;
import graphql.schema.DataFetchingEnvironment;

/**
 * GraphQL query implementation for trainsByRoute.
 * Matches the REST API endpoint: /live-trains/station/{departure_station}/{arrival_station}
 */
@Component
public class TrainsByRouteQuery extends BaseQuery<TrainTO> {

    private static final int MAX_ROUTE_SEARCH_RESULT_SIZE = 1000;
    private static final int DAYS_BETWEEN_LIMIT = 2;

    @Autowired
    private TrainRepository trainRepository;

    @Autowired
    private TrainTOConverter trainTOConverter;

    @Override
    public String getQueryName() {
        return "trainsByRoute";
    }

    @Override
    public Class getEntityClass() {
        return Train.class;
    }

    @Override
    public Expression[] getFields() {
        return AllFields.TRAIN;
    }

    @Override
    public EntityPath getEntityTable() {
        return QTrain.train;
    }

    @Override
    public BooleanExpression createWhereFromArguments(final DataFetchingEnvironment env) {
        final String departureStation = env.getArgument("departureStation");
        final String arrivalStation = env.getArgument("arrivalStation");
        final LocalDate departureDate = env.getArgument("departureDate");

        // GraphQL provides DateTime as OffsetDateTime, convert to ZonedDateTime
        final OffsetDateTime startDateOffset = env.getArgument("startDate");
        final OffsetDateTime endDateOffset = env.getArgument("endDate");
        final ZonedDateTime startDate = startDateOffset != null ? startDateOffset.atZoneSameInstant(ZoneId.of("Europe/Helsinki")) : null;
        final ZonedDateTime endDate = endDateOffset != null ? endDateOffset.atZoneSameInstant(ZoneId.of("Europe/Helsinki")) : null;

        final Integer limit = env.getArgument("limit");
        final Boolean includeNonStopping = env.getArgument("includeNonStopping");

        // Validate required parameters
        if (departureStation == null || arrivalStation == null) {
            throw new CustomException(400, "departureStation and arrivalStation are required");
        }

        // Validate date range if both startDate and endDate are provided
        if (startDate != null && endDate != null) {
            final int daysBetween = (int) Duration.between(startDate, endDate).toDays();
            if (daysBetween > DAYS_BETWEEN_LIMIT) {
                throw new CustomException(400, String.format("Date range too long. Maximum %d days allowed, got %d days",
                    DAYS_BETWEEN_LIMIT, daysBetween));
            }
        }

        // Calculate effective parameters matching REST API logic
        final ZonedDateTime actualTrainStart;
        final ZonedDateTime actualTrainEnd;
        final LocalDate departureDateStart;
        final LocalDate departureDateEnd;

        if (departureDate != null) {
            // Use departure_date parameter with wider range to catch late/early trains
            actualTrainStart = departureDate.minusDays(1).atStartOfDay(ZoneId.of("Europe/Helsinki"));
            actualTrainEnd = departureDate.plusDays(2).atStartOfDay(ZoneId.of("Europe/Helsinki"));
            departureDateStart = departureDate;
            departureDateEnd = departureDate;
        } else {
            if (startDate == null && endDate == null) {
                // Default: search from now to +24 hours
                actualTrainStart = ZonedDateTime.now(ZoneId.of("Europe/Helsinki"));
                actualTrainEnd = actualTrainStart.plusHours(24);
            } else if (startDate != null && endDate == null) {
                // Only start date provided
                actualTrainStart = startDate;
                actualTrainEnd = actualTrainStart.plusHours(24);
            } else if (startDate != null && endDate != null) {
                // Both dates provided
                if (endDate.isBefore(startDate)) {
                    throw new CustomException(400, "endDate cannot be before startDate");
                }
                actualTrainStart = startDate;
                actualTrainEnd = endDate;
            } else {
                // Only end date provided (invalid)
                throw new CustomException(400, "endDate provided without startDate");
            }

            departureDateStart = actualTrainStart.minusDays(1).toLocalDate();
            departureDateEnd = actualTrainEnd.plusDays(1).toLocalDate();
        }

        // Apply limit (default to 1000 if not specified)
        final int effectiveLimit = limit != null ? Math.min(limit, MAX_ROUTE_SEARCH_RESULT_SIZE) : MAX_ROUTE_SEARCH_RESULT_SIZE;

        // Default includeNonStopping to false to match REST API
        final boolean effectiveIncludeNonStopping = includeNonStopping != null ? includeNonStopping : false;

        // Get matching train IDs from repository using the same query as REST API
        final List<TrainId> trainIds = trainRepository.findByStationsAndScheduledDate(
            departureStation.toUpperCase(),
            arrivalStation.toUpperCase(),
            actualTrainStart,
            actualTrainEnd,
            departureDateStart,
            departureDateEnd,
            effectiveIncludeNonStopping,
            effectiveLimit
        );

        if (trainIds.isEmpty()) {
            trainIds.add(new TrainId(-9999L, LocalDate.now()));
        }

        return TrainIdOptimizer.optimize(QTrain.train.id, trainIds);
    }

    @Override
    public TrainTO convertEntityToTO(final Tuple tuple) {
        return trainTOConverter.convert(tuple);
    }
}

