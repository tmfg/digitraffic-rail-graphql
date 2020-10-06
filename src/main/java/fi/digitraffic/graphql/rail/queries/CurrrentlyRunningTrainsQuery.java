package fi.digitraffic.graphql.rail.queries;

import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import fi.digitraffic.graphql.rail.entities.QTrain;
import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.querydsl.AllFields;
import fi.digitraffic.graphql.rail.to.TrainTOConverter;
import graphql.schema.DataFetchingEnvironment;

@Component
public class CurrrentlyRunningTrainsQuery extends BaseQuery<TrainTO> {

    @Autowired
    private TrainTOConverter trainTOConverter;

    @Override
    public String getQueryName() {
        return "currentlyRunningTrains";
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
    public BooleanExpression createWhereFromArguments(DataFetchingEnvironment dataFetchingEnvironment) {
        LocalDate departureDate = LocalDate.now(ZoneId.of("Europe/Helsinki"));
        LocalDate yesterday = departureDate.minusDays(1);

        BooleanExpression fi = QTrain.train.timeTableRows.any().stationShortCode.eq("OL");

        return QTrain.train.id.departureDate.in(departureDate, yesterday).and(QTrain.train.runningCurrently.eq(true)).and(fi);
    }

    @Override
    public TrainTO convertEntityToTO(Tuple tuple) {
        return trainTOConverter.convert(tuple);
    }
}
