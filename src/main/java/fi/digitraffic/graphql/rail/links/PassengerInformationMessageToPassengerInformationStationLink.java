package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;

import fi.digitraffic.graphql.rail.entities.PassengerInformationStation;
import fi.digitraffic.graphql.rail.entities.QPassengerInformationStation;
import fi.digitraffic.graphql.rail.links.base.OneToManyLink;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationStationTO;
import fi.digitraffic.graphql.rail.querydsl.AllFields;
import fi.digitraffic.graphql.rail.to.PassengerInformationStationTOConverter;

@Component
public class PassengerInformationMessageToPassengerInformationStationLink
        extends
        OneToManyLink<String, PassengerInformationMessageTO, PassengerInformationStation, PassengerInformationStationTO> {
    @Autowired
    private PassengerInformationStationTOConverter passengerInformationStationTOConverter;

    @Override
    public String getTypeName() {
        return "PassengerInformationMessage";
    }

    @Override
    public String getFieldName() {
        return "stations";
    }

    @Override
    public String createKeyFromParent(final PassengerInformationMessageTO passengerInformationMessageTO) {
        return passengerInformationMessageTO.getId() + "-" + String.valueOf(passengerInformationMessageTO.getVersion());
    }

    @Override
    public String createKeyFromChild(final PassengerInformationStationTO stationTO) {
        return stationTO.getMessageId() + "-" + String.valueOf(stationTO.getMessageVersion());
    }

    @Override
    public PassengerInformationStationTO createChildTOFromTuple(final Tuple tuple) {
        return passengerInformationStationTOConverter.convert(tuple);
    }

    @Override
    public Class getEntityClass() {
        return PassengerInformationStation.class;
    }

    @Override
    public Expression[] getFields() {
        return AllFields.PASSENGER_INFORMATION_STATION;
    }

    @Override
    public EntityPath getEntityTable() {
        return QPassengerInformationStation.passengerInformationStation;
    }

    @Override
    public BooleanExpression createWhere(final List<String> keys) {
        return keys.stream()
                .map(key -> {
                    final String[] parts = key.split("-");
                    final String messageId = parts[0];
                    final int messageVersion = Integer.parseInt(parts[1]);

                    return QPassengerInformationStation.passengerInformationStation.message.id.id.eq(messageId)
                            .and(QPassengerInformationStation.passengerInformationStation.message.id.version.eq(messageVersion));
                })
                .reduce(BooleanExpression::or)
                .orElse(null);
    }

}
