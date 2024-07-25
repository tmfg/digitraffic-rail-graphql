package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessage;
import fi.digitraffic.graphql.rail.entities.PassengerInformationMessageId;
import fi.digitraffic.graphql.rail.entities.QPassengerInformationMessage;
import fi.digitraffic.graphql.rail.links.base.OneToOneLink;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageStationTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageTO;
import fi.digitraffic.graphql.rail.querydsl.AllFields;
import fi.digitraffic.graphql.rail.to.PassengerInformationMessageTOConverter;

@Component
public class PassengerInformationMessageStationToPassengerInformationMessageLink
        extends
        OneToOneLink<PassengerInformationMessageId, PassengerInformationMessageStationTO, PassengerInformationMessage, PassengerInformationMessageTO> {
    @Autowired
    private PassengerInformationMessageTOConverter passengerInformationMessageTOConverter;

    @Override
    public String getTypeName() {
        return "PassengerInformationMessageStation";
    }

    @Override
    public String getFieldName() {
        return "message";
    }

    @Override
    public PassengerInformationMessageId createKeyFromParent(final PassengerInformationMessageStationTO passengerInformationMessageStationTO) {
        return new PassengerInformationMessageId(passengerInformationMessageStationTO.getMessageId(),
                passengerInformationMessageStationTO.getMessageVersion());
    }

    @Override
    public PassengerInformationMessageId createKeyFromChild(final PassengerInformationMessageTO passengerInformationMessageTO) {
        return new PassengerInformationMessageId(passengerInformationMessageTO.getId(), passengerInformationMessageTO.getVersion());
    }

    @Override
    public PassengerInformationMessageTO createChildTOFromTuple(final Tuple tuple) {
        return passengerInformationMessageTOConverter.convert(tuple);
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
    public BooleanExpression createWhere(final List<PassengerInformationMessageId> keys) {
        return QPassengerInformationMessage.passengerInformationMessage.id.in(keys);
    }

}
