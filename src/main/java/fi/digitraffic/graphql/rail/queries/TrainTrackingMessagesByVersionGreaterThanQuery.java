package fi.digitraffic.graphql.rail.queries;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import fi.digitraffic.graphql.rail.entities.QTrainTrackingMessage;
import fi.digitraffic.graphql.rail.entities.TrainTrackingMessage;
import fi.digitraffic.graphql.rail.model.TrainTrackingMessageTO;
import fi.digitraffic.graphql.rail.querydsl.AllFields;
import fi.digitraffic.graphql.rail.to.TrainTrackingTOConverter;
import graphql.schema.DataFetchingEnvironment;

@Component
public class TrainTrackingMessagesByVersionGreaterThanQuery extends BaseQuery<TrainTrackingMessageTO> {
    @Autowired
    private TrainTrackingTOConverter trainTrackingTOConverter;

    @Override
    public String getQueryName() {
        return "trainTrackingMessagesByVersionGreaterThan";
    }

    @Override
    public Class getEntityClass() {
        return TrainTrackingMessage.class;
    }

    @Override
    public Expression[] getFields() {
        return AllFields.TRAIN_TRACKING_MESSAGE;
    }

    @Override
    public EntityPath getEntityTable() {
        return QTrainTrackingMessage.trainTrackingMessage;
    }

    @Override
    public BooleanExpression createWhereFromArguments(DataFetchingEnvironment dataFetchingEnvironment) {
        Long version = Long.parseLong(dataFetchingEnvironment.getArgument("version"));
        return QTrainTrackingMessage.trainTrackingMessage.version.gt(version);
    }

    @Override
    protected JPAQuery<Tuple> createLimitQuery(JPAQuery<Tuple> query, Object limitArgument) {
        return super.createLimitQuery(query, 2000);
    }

    @Override
    public TrainTrackingMessageTO convertEntityToTO(Tuple tuple) {
        return trainTrackingTOConverter.convert(tuple);
    }

    @Override
    public OrderSpecifier createDefaultOrder() {
        return new OrderSpecifier(Order.ASC, QTrainTrackingMessage.trainTrackingMessage.version);
    }
}
