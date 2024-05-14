package fi.digitraffic.graphql.rail.queries;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessage;
import fi.digitraffic.graphql.rail.entities.QPassengerInformationMessage;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageTO;
import fi.digitraffic.graphql.rail.querydsl.AllFields;
import fi.digitraffic.graphql.rail.to.PassengerInformationMessageTOConverter;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Component
public class PassengerInformationMessagesQuery extends BaseQuery<PassengerInformationMessageTO> {

    @Autowired
    private PassengerInformationMessageTOConverter passengerInformationMessageTOConverter;

    @Override
    public String getQueryName() {
        return "passengerInformationMessages";
    }

    @Override
    public Class getEntityClass() {
        return PassengerInformationMessage.class;
    }

    @Override
    public Expression[] getFields() {
        return AllFields.PASSENGER_INFORMATION_MESSAGE;
    }

    @Override
    public EntityPath getEntityTable() {
        return QPassengerInformationMessage.passengerInformationMessage;
    }

    @Override
    public BooleanExpression createWhereFromArguments(final DataFetchingEnvironment dataFetchingEnvironment) {
        return QPassengerInformationMessage.passengerInformationMessage.endValidity.after(ZonedDateTime.now()).and(
                QPassengerInformationMessage.passengerInformationMessage.startValidity.before(ZonedDateTime.now()));
    }

    @Override
    public PassengerInformationMessageTO convertEntityToTO(final Tuple tuple) {
        return passengerInformationMessageTOConverter.convert(tuple);
    }

    @Override
    public OrderSpecifier createDefaultOrder() {
        return new OrderSpecifier(Order.ASC, QPassengerInformationMessage.passengerInformationMessage.creationDateTime);
    }

    @Override
    public DataFetcher<List<PassengerInformationMessageTO>> createFetcher() {
        final Expression<?>[] allFields = Stream.of(
                QPassengerInformationMessage.passengerInformationMessage).toArray(Expression<?>[]::new);

        final JPAQuery<Tuple> queryAfterFrom = super.queryFactory.selectDistinct(allFields)
                .from(getEntityTable())
                .leftJoin(QPassengerInformationMessage.passengerInformationMessage.audio).fetchJoin()
                .leftJoin(QPassengerInformationMessage.passengerInformationMessage.video).fetchJoin();
        
        return super.createFetcher(queryAfterFrom);
    }
}
