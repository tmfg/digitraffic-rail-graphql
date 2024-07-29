package fi.digitraffic.graphql.rail.queries;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessage;
import fi.digitraffic.graphql.rail.entities.QPassengerInformationMessage;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageTO;
import fi.digitraffic.graphql.rail.querydsl.AllFields;
import fi.digitraffic.graphql.rail.to.PassengerInformationMessageTOConverter;
import graphql.execution.AbortExecutionException;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import jakarta.persistence.QueryTimeoutException;

@Component
public class PassengerInformationMessagesQuery extends BaseQuery<PassengerInformationMessageTO> {

    @Autowired
    private PassengerInformationMessageTOConverter passengerInformationMessageTOConverter;

    public static BooleanExpression getMessageValidityConditions() {
        return QPassengerInformationMessage.passengerInformationMessage.deleted.isNull()
                .and(QPassengerInformationMessage.passengerInformationMessage.startValidity.before(ZonedDateTime.now())
                        .and(QPassengerInformationMessage.passengerInformationMessage.endValidity.after(ZonedDateTime.now())));
    }

    public static JPAQuery<Tuple> getPassengerInformationBaseQuery(final JPAQueryFactory jpaQueryFactory, final EntityPath entityTable) {
        final JPAQuery<Tuple> maxVersions = jpaQueryFactory.select(
                        QPassengerInformationMessage.passengerInformationMessage.id.id,
                        QPassengerInformationMessage.passengerInformationMessage.id.version.max())
                .from(QPassengerInformationMessage.passengerInformationMessage)
                .groupBy(QPassengerInformationMessage.passengerInformationMessage.id.id);

        return jpaQueryFactory.selectDistinct(AllFields.PASSENGER_INFORMATION_MESSAGE)
                .from(entityTable)
                .where(Expressions.list(QPassengerInformationMessage.passengerInformationMessage.id.id,
                                QPassengerInformationMessage.passengerInformationMessage.id.version).in(maxVersions)
                        .and(getMessageValidityConditions()));
    }

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
        return QPassengerInformationMessage.passengerInformationMessage.startValidity.before(ZonedDateTime.now())
                .and(QPassengerInformationMessage.passengerInformationMessage.endValidity.after(ZonedDateTime.now()));
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

        return dataFetchingEnvironment -> {
            final Class entityClass = getEntityClass();
            final PathBuilder<PassengerInformationMessageTO> pathBuilder = new PathBuilder<>(entityClass,
                    entityClass.getSimpleName().substring(0, 1).toLowerCase() + entityClass.getSimpleName().substring(1));

            final BooleanExpression basicWhere = createWhereFromArguments(dataFetchingEnvironment);

            final JPAQuery<Tuple> queryAfterFrom = getPassengerInformationBaseQuery(super.queryFactory, getEntityTable());

            final JPAQuery<Tuple> queryAfterWhere =
                    createWhereQuery(queryAfterFrom, pathBuilder, basicWhere, dataFetchingEnvironment.getArgument("where"));
            final JPAQuery<Tuple> queryAfterOrderBy =
                    createOrderByQuery(queryAfterWhere, pathBuilder, dataFetchingEnvironment.getArgument("orderBy"));
            final JPAQuery<Tuple> queryAfterOffset = createOffsetQuery(queryAfterOrderBy, dataFetchingEnvironment.getArgument("skip"));
            final JPAQuery<Tuple> queryAfterLimit = createLimitQuery(queryAfterOffset, dataFetchingEnvironment.getArgument("take"));

            try {
                final List<Tuple> rows = queryAfterLimit.fetch();
                return rows.stream().map(s -> convertEntityToTO(s)).collect(Collectors.toList());
            } catch (final QueryTimeoutException e) {
                throw new AbortExecutionException(e);
            }
        };

    }
}
