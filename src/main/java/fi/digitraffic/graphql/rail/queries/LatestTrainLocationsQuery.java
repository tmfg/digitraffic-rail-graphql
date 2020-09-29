package fi.digitraffic.graphql.rail.queries;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import fi.digitraffic.graphql.rail.config.graphql.AllFields;
import fi.digitraffic.graphql.rail.entities.QTrainLocation;
import fi.digitraffic.graphql.rail.entities.TrainLocation;
import fi.digitraffic.graphql.rail.model.TrainLocationTO;
import fi.digitraffic.graphql.rail.repositories.TrainLocationRepository;
import fi.digitraffic.graphql.rail.to.TrainLocationTOConverter;
import graphql.schema.DataFetchingEnvironment;

@Component
public class LatestTrainLocationsQuery extends BaseQuery<TrainLocationTO> {

    @Autowired
    private TrainLocationRepository trainLocationRepository;

    @Autowired
    private TrainLocationTOConverter trainLocationTOConverter;

    @Override
    public String getQueryName() {
        return "latestTrainLocations";
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
    public BooleanExpression createWhereFromArguments(DataFetchingEnvironment dataFetchingEnvironment) {
        List<Long> ids = trainLocationRepository.findLatest(ZonedDateTime.now(ZoneId.of("Europe/Helsinki")).minusMinutes(15));
        return QTrainLocation.trainLocation.id.in(ids);
    }

    @Override
    public TrainLocationTO convertEntityToTO(Tuple tuple) {
        return trainLocationTOConverter.convert(tuple);
    }
}
