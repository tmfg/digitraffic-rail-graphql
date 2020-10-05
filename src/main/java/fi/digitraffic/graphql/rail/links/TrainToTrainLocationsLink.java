package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import fi.digitraffic.graphql.rail.entities.QTrainLocation;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.entities.TrainLocation;
import fi.digitraffic.graphql.rail.links.base.OneToManyLink;
import fi.digitraffic.graphql.rail.model.TrainLocationTO;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.querydsl.AllFields;
import fi.digitraffic.graphql.rail.to.TrainLocationTOConverter;

@Component
public class TrainToTrainLocationsLink extends OneToManyLink<TrainId, TrainTO, TrainLocation, TrainLocationTO> {
    @Autowired
    private TrainLocationTOConverter trainLocationTOConverter;

    @Override
    public String getTypeName() {
        return "Train";
    }

    @Override
    public String getFieldName() {
        return "trainLocations";
    }

    @Override
    public TrainId createKeyFromParent(TrainTO trainTO) {
        return new TrainId(trainTO.getTrainNumber().longValue(), trainTO.getDepartureDate());
    }

    @Override
    public TrainId createKeyFromChild(TrainLocationTO trainLocationTO) {
        return new TrainId(trainLocationTO.getTrainNumber().longValue(), trainLocationTO.getDepartureDate());
    }

    @Override
    public TrainLocationTO createChildTOFromTuple(Tuple tuple) {
        return trainLocationTOConverter.convert(tuple);
    }

    @Override
    public Class getEntityClass() {
        return TrainLocation.class;
    }

    @Override
    public Expression[] getFields() {
        return AllFields.TRAIN_LOCATION;
    }

    @Override
    public EntityPath getEntityTable() {
        return QTrainLocation.trainLocation;
    }

    @Override
    public BooleanExpression createWhere(List<TrainId> keys) {
        return QTrainLocation.trainLocation.train.id.in(keys);
    }
}
