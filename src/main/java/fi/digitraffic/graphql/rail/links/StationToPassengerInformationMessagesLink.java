package fi.digitraffic.graphql.rail.links;

import static fi.digitraffic.graphql.rail.queries.PassengerInformationMessagesQuery.getPassengerInformationBaseQuery;

import java.util.List;
import java.util.stream.Collectors;

import org.dataloader.BatchLoaderWithContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessage;
import fi.digitraffic.graphql.rail.entities.QPassengerInformationMessage;
import fi.digitraffic.graphql.rail.links.base.ManyToManyLink;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageTO;
import fi.digitraffic.graphql.rail.model.StationTO;
import fi.digitraffic.graphql.rail.querydsl.AllFields;
import fi.digitraffic.graphql.rail.to.PassengerInformationMessageTOConverter;

@Component
public class StationToPassengerInformationMessagesLink extends
        ManyToManyLink<String, StationTO, PassengerInformationMessage, PassengerInformationMessageTO> {

    @Autowired
    private PassengerInformationMessageTOConverter passengerInformationMessageTOConverter;

    @Override
    public String getTypeName() {
        return "Station";
    }

    @Override
    public String getFieldName() {
        return "passengerInformationMessages";
    }

    @Override
    public String createKeyFromParent(final StationTO stationTO) {
        return stationTO.getShortCode();
    }

    @Override
    public List<String> createKeysFromChild(final PassengerInformationMessageTO child) {
        if (child == null) {
            return null;
        }
        return child.getStations().stream().map(station -> station.getStationShortCode()).collect(Collectors.toList());
    }

    @Override
    public PassengerInformationMessageTO createChildTOFromTuple(final Tuple tuple) {
        return passengerInformationMessageTOConverter.convert(tuple);
    }

    @Override
    public Class<PassengerInformationMessage> getEntityClass() {
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
    public BooleanExpression createWhere(final List<String> keys) {
        return QPassengerInformationMessage.passengerInformationMessage.stations.any().stationShortCode.in(keys);
    }

    @Override
    public BatchLoaderWithContext<String, List<PassengerInformationMessageTO>> createLoader() {
        final JPAQuery<Tuple> queryAfterFrom = getPassengerInformationBaseQuery(super.queryFactory, getEntityTable());
        return doCreateLoader(queryAfterFrom);
    }

}

