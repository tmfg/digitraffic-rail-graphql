package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessage;
import fi.digitraffic.graphql.rail.entities.QPassengerInformationMessage;
import fi.digitraffic.graphql.rail.links.base.OneToOneLink;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageStationTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageTO;
import fi.digitraffic.graphql.rail.querydsl.AllFields;
import fi.digitraffic.graphql.rail.to.PassengerInformationMessageTOConverter;

@Component
public class PassengerInformationMessageStationToPassengerInformationMessageLink
        extends
        OneToOneLink<String, PassengerInformationMessageStationTO, PassengerInformationMessage, PassengerInformationMessageTO> {
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
    public String createKeyFromParent(final PassengerInformationMessageStationTO passengerInformationMessageStationTO) {
        return passengerInformationMessageStationTO.getMessageId() + "-" + String.valueOf(passengerInformationMessageStationTO.getMessageVersion());
    }

    @Override
    public String createKeyFromChild(final PassengerInformationMessageTO passengerInformationMessageTO) {
        return passengerInformationMessageTO.getId() + "-" + String.valueOf(passengerInformationMessageTO.getVersion());
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
    public BooleanExpression createWhere(final List<String> keys) {
        return keys.stream()
                .map(key -> {
                    final String[] parts = key.split("-");
                    final String messageId = parts[0];
                    final int messageVersion = Integer.parseInt(parts[1]);

                    return QPassengerInformationMessage.passengerInformationMessage.id.id.eq(messageId)
                            .and(QPassengerInformationMessage.passengerInformationMessage.id.version.eq(messageVersion));
                })
                .reduce(BooleanExpression::or)
                .orElse(null);
    }

}
