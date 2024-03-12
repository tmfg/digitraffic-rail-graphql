package fi.digitraffic.graphql.rail.queries;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.BooleanExpression;
import fi.digitraffic.graphql.rail.entities.QTrain;
import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.to.TrainTOConverter;
import graphql.schema.DataFetchingEnvironment;

@Component
public class TrainsByDepartureDateQuery extends BaseQuery<TrainTO> {
    @Autowired
    private TrainTOConverter trainTOConverter;

    @Override
    public String getQueryName() {
        return "trainsByDepartureDate";
    }

    @Override
    public Class getEntityClass() {
        return Train.class;
    }

    @Override
    public EntityPath getEntityTable() {
        return QTrain.train;
    }

    @Override
    public BooleanExpression createWhereFromArguments(DataFetchingEnvironment dataFetchingEnvironment) {
        LocalDate departureDate = dataFetchingEnvironment.getArgument("departureDate");
        return QTrain.train.id.departureDate.eq(departureDate);
    }

    @Override
    public TrainTO convertEntityToTO(Tuple tuple) {
        return trainTOConverter.convert(tuple);
    }


}
