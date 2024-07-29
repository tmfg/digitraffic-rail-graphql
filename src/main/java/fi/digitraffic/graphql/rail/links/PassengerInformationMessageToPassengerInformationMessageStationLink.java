package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessageId;
import fi.digitraffic.graphql.rail.entities.PassengerInformationMessageStation;
import fi.digitraffic.graphql.rail.entities.QPassengerInformationMessageStation;
import fi.digitraffic.graphql.rail.links.base.OneToManyLink;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageStationTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageTO;
import fi.digitraffic.graphql.rail.querydsl.AllFields;
import fi.digitraffic.graphql.rail.to.PassengerInformationMessageStationTOConverter;

@Component
public class PassengerInformationMessageToPassengerInformationMessageStationLink
        extends
        OneToManyLink<PassengerInformationMessageId, PassengerInformationMessageTO, PassengerInformationMessageStation, PassengerInformationMessageStationTO> {
    @Autowired
    private PassengerInformationMessageStationTOConverter passengerInformationMessageStationTOConverter;

    @Override
    public String getTypeName() {
        return "PassengerInformationMessage";
    }

    @Override
    public String getFieldName() {
        return "messageStations";
    }

    @Override
    public PassengerInformationMessageId createKeyFromParent(final PassengerInformationMessageTO passengerInformationMessageTO) {
        return new PassengerInformationMessageId(passengerInformationMessageTO.getId(), passengerInformationMessageTO.getVersion());
    }

    @Override
    public PassengerInformationMessageId createKeyFromChild(final PassengerInformationMessageStationTO passengerInformationMessageStationTO) {
        return new PassengerInformationMessageId(passengerInformationMessageStationTO.getMessageId(),
                passengerInformationMessageStationTO.getMessageVersion());
    }

    @Override
    public PassengerInformationMessageStationTO createChildTOFromTuple(final Tuple tuple) {
        return passengerInformationMessageStationTOConverter.convert(tuple);
    }

    @Override
    public Class getEntityClass() {
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
    public BooleanExpression createWhere(final List<PassengerInformationMessageId> keys) {
        return QPassengerInformationMessageStation.passengerInformationMessageStation.message.id.in(keys);
    }

}
