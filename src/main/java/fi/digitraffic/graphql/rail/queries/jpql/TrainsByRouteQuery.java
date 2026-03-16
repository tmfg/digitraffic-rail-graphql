package fi.digitraffic.graphql.rail.queries.jpql;

import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.config.graphql.CustomException;
import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.links.base.jpql.TrainIdJpqlWhereClause;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.querydsl.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.querydsl.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.repositories.TrainRepository;
import fi.digitraffic.graphql.rail.to.TrainTOConverter;
import graphql.schema.DataFetchingEnvironment;

@Component
public class TrainsByRouteQuery extends BaseQueryJpql<Train, TrainTO> {

    private static final int MAX_ROUTE_SEARCH_RESULT_SIZE = 1000;
    private static final int DAYS_BETWEEN_LIMIT = 2;
    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("Europe/Helsinki");

    private final TrainRepository trainRepository;
    private final TrainTOConverter trainTOConverter;

    public TrainsByRouteQuery(final JpqlWhereBuilder whereBuilder,
                              final JpqlOrderByBuilder orderByBuilder,
                              @Value("${digitraffic.max-returned-rows}") final int maxResults,
                              final TrainRepository trainRepository,
                              final TrainTOConverter trainTOConverter) {
        super(whereBuilder, orderByBuilder, maxResults);
        this.trainRepository = trainRepository;
        this.trainTOConverter = trainTOConverter;
    }

    @Override
    public String getQueryName() {
        return "trainsByRoute";
    }

    @Override
    public Class<Train> getEntityClass() {
        return Train.class;
    }

    @Override
    public String buildBaseWhereClause(final String alias, final DataFetchingEnvironment env,
                                       final Map<String, Object> parameters) {
        final String departureStation = env.getArgument("departureStation");
        final String arrivalStation = env.getArgument("arrivalStation");
        final LocalDate departureDate = env.getArgument("departureDate");

        final OffsetDateTime startDateOffset = env.getArgument("startDate");
        final OffsetDateTime endDateOffset = env.getArgument("endDate");
        final ZonedDateTime startDate = startDateOffset != null ? startDateOffset.atZoneSameInstant(DEFAULT_ZONE_ID) : null;
        final ZonedDateTime endDate = endDateOffset != null ? endDateOffset.atZoneSameInstant(DEFAULT_ZONE_ID) : null;

        final Integer limit = env.getArgument("limit");
        final Boolean includeNonStopping = env.getArgument("includeNonStopping");

        if (departureStation == null || arrivalStation == null) {
            throw new CustomException(400, "departureStation and arrivalStation are required");
        }

        if (startDate != null && endDate != null) {
            final int daysBetween = (int) Duration.between(startDate, endDate).toDays();
            if (daysBetween > DAYS_BETWEEN_LIMIT) {
                throw new CustomException(400, String.format("Date range too long. Maximum %d days allowed, got %d days",
                        DAYS_BETWEEN_LIMIT, daysBetween));
            }
        }

        final ZonedDateTime actualTrainStart;
        final ZonedDateTime actualTrainEnd;
        final LocalDate departureDateStart;
        final LocalDate departureDateEnd;

        if (departureDate != null) {
            actualTrainStart = departureDate.minusDays(1).atStartOfDay(DEFAULT_ZONE_ID);
            actualTrainEnd = departureDate.plusDays(2).atStartOfDay(DEFAULT_ZONE_ID);
            departureDateStart = departureDate;
            departureDateEnd = departureDate;
        } else {
            if (startDate == null && endDate == null) {
                actualTrainStart = ZonedDateTime.now(DEFAULT_ZONE_ID);
                actualTrainEnd = actualTrainStart.plusHours(24);
            } else if (startDate != null && endDate == null) {
                actualTrainStart = startDate;
                actualTrainEnd = actualTrainStart.plusHours(24);
            } else if (endDate != null) { // startDate != null is guaranteed by the previous branch
                if (endDate.isBefore(startDate)) {
                    throw new CustomException(400, "endDate cannot be before startDate");
                }
                actualTrainStart = startDate;
                actualTrainEnd = endDate;
            } else {
                throw new CustomException(400, "endDate provided without startDate");
            }

            departureDateStart = actualTrainStart.minusDays(1).toLocalDate();
            departureDateEnd = actualTrainEnd.plusDays(1).toLocalDate();
        }

        final int effectiveLimit = limit != null ? Math.min(limit, MAX_ROUTE_SEARCH_RESULT_SIZE) : MAX_ROUTE_SEARCH_RESULT_SIZE;
        final boolean effectiveIncludeNonStopping = includeNonStopping != null ? includeNonStopping : false;

        final List<TrainId> trainIds = trainRepository.findByStationsAndScheduledDate(
                departureStation.toUpperCase(),
                arrivalStation.toUpperCase(),
                actualTrainStart,
                actualTrainEnd,
                departureDateStart,
                departureDateEnd,
                effectiveIncludeNonStopping,
                effectiveLimit);

        if (trainIds.isEmpty()) {
            return "1 = 0";
        }

        final var keyWhere = TrainIdJpqlWhereClause.build(alias, "id.departureDate", "id.trainNumber", trainIds);
        parameters.putAll(keyWhere.params());
        return keyWhere.jpql();
    }

    @Override
    public TrainTO convertEntityToTO(final Train entity) {
        return trainTOConverter.convertEntity(entity);
    }
}


