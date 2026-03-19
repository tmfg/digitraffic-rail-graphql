package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.entities.TrainLocation;
import fi.digitraffic.graphql.rail.links.base.OneToManyLink;
import fi.digitraffic.graphql.rail.links.base.TrainIdWhereClause;
import fi.digitraffic.graphql.rail.model.TrainLocationTO;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.links.base.KeyWhereClause;
import fi.digitraffic.graphql.rail.queries.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.queries.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.TrainLocationTOConverter;

@Component
public class TrainToTrainLocationsLink extends OneToManyLink<TrainId, TrainTO, TrainLocation, TrainLocationTO> {

    private final TrainLocationTOConverter trainLocationTOConverter;

    public TrainToTrainLocationsLink(final JpqlWhereBuilder jpqlWhereBuilder,
                                     final JpqlOrderByBuilder jpqlOrderByBuilder,
                                     @Value("${digitraffic.batch-load-size:500}") final int batchLoadSize,
                                     final TrainLocationTOConverter trainLocationTOConverter) {
        super(jpqlWhereBuilder, jpqlOrderByBuilder, batchLoadSize);
        this.trainLocationTOConverter = trainLocationTOConverter;
    }

    @Override
    public String getTypeName() {
        return "Train";
    }

    @Override
    public String getFieldName() {
        return "trainLocations";
    }

    @Override
    public TrainId createKeyFromParent(final TrainTO trainTO) {
        return new TrainId(trainTO.getTrainNumber(), trainTO.getDepartureDate());
    }

    @Override
    public TrainId createKeyFromChild(final TrainLocationTO trainLocationTO) {
        return new TrainId(trainLocationTO.getTrainNumber(), trainLocationTO.getDepartureDate());
    }

    @Override
    public TrainLocationTO createChildTOFromEntity(final TrainLocation entity) {
        return trainLocationTOConverter.convertEntity(entity);
    }

    @Override
    public Class<TrainLocation> getEntityClass() {
        return TrainLocation.class;
    }


    @Override
    protected KeyWhereClause buildKeyWhereClause(final List<TrainId> keys) {
        // TrainLocation has flat departureDate and trainNumber columns (not inside an embedded id)
        return TrainIdWhereClause.build(getEntityAlias(), "departureDate", "trainNumber", keys);
    }

    @Override
    public String getDefaultOrderBy() {
        return getEntityAlias() + ".trainLocationId.timestamp ASC";
    }
}

