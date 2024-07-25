package fi.digitraffic.graphql.rail.links;

import static fi.digitraffic.graphql.rail.queries.PassengerInformationMessagesQuery.getMessageValidityConditions;

import java.util.List;
import java.util.function.Function;

import org.dataloader.BatchLoaderWithContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessageStation;
import fi.digitraffic.graphql.rail.entities.QPassengerInformationMessage;
import fi.digitraffic.graphql.rail.entities.QPassengerInformationMessageStation;
import fi.digitraffic.graphql.rail.links.base.OneToManyLink;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageStationTO;
import fi.digitraffic.graphql.rail.model.StationTO;
import fi.digitraffic.graphql.rail.querydsl.AllFields;
import fi.digitraffic.graphql.rail.to.PassengerInformationMessageStationTOConverter;

@Component
public class StationToPassengerInformationStationLink extends
        OneToManyLink<String, StationTO, PassengerInformationMessageStation, PassengerInformationMessageStationTO> {

    public static JPAQuery<Tuple> getPassengerInformationStationBaseQuery(final JPAQueryFactory jpaQueryFactory, final EntityPath entityTable) {

        final JPAQuery<Tuple> maxVersions = jpaQueryFactory.select(
                        QPassengerInformationMessageStation.passengerInformationMessageStation.messageId,
                        QPassengerInformationMessageStation.passengerInformationMessageStation.messageVersion.max())
                .from(QPassengerInformationMessageStation.passengerInformationMessageStation)
                .groupBy(QPassengerInformationMessageStation.passengerInformationMessageStation.messageId);

        return jpaQueryFactory.selectDistinct(AllFields.PASSENGER_INFORMATION_MESSAGE_STATION)
                .from(entityTable)
                .leftJoin(QPassengerInformationMessage.passengerInformationMessage)
                .on(QPassengerInformationMessage.passengerInformationMessage.id.id.eq(
                                QPassengerInformationMessageStation.passengerInformationMessageStation.messageId)
                        .and(QPassengerInformationMessage.passengerInformationMessage.id.version.eq(
                                QPassengerInformationMessageStation.passengerInformationMessageStation.messageVersion)))
                .where(Expressions.list(QPassengerInformationMessageStation.passengerInformationMessageStation.messageId,
                                QPassengerInformationMessageStation.passengerInformationMessageStation.messageVersion).in(maxVersions)
                        .and(getMessageValidityConditions()));

    }

    @Autowired
    private PassengerInformationMessageStationTOConverter passengerInformationMessageStationTOConverter;

    @Override
    public String getTypeName() {
        return "Station";
    }

    @Override
    public String getFieldName() {
        return "stationMessages";
    }

    @Override
    public String createKeyFromParent(final StationTO stationTO) {
        return stationTO.getShortCode();
    }

    @Override
    public String createKeyFromChild(final PassengerInformationMessageStationTO child) {
        if (child == null) {
            return null;
        }

        return child.getStationShortCode();
    }

    @Override
    public PassengerInformationMessageStationTO createChildTOFromTuple(final Tuple tuple) {
        return passengerInformationMessageStationTOConverter.convert(tuple);
    }

    @Override
    public Class<PassengerInformationMessageStation> getEntityClass() {
        return PassengerInformationMessageStation.class;
    }

    @Override
    public Expression[] getFields() {
        return AllFields.PASSENGER_INFORMATION_MESSAGE_STATION;
    }

    @Override
    public EntityPath getEntityTable() {
        return QPassengerInformationMessageStation.passengerInformationMessageStation;
    }

    @Override
    public BooleanExpression createWhere(final List<String> keys) {
        return QPassengerInformationMessageStation.passengerInformationMessageStation.stationShortCode.in(keys);
    }

    @Override
    public BatchLoaderWithContext<String, List<PassengerInformationMessageStationTO>> createLoader() {
        final Function<JPAQueryFactory, JPAQuery<Tuple>> queryAfterFromFunction = (queryFactory) -> {
            return getPassengerInformationStationBaseQuery(queryFactory, getEntityTable());
        };

        return doCreateLoader(queryAfterFromFunction);
    }

}

