package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;

import fi.digitraffic.graphql.rail.entities.PassengerInformationAudio;
import fi.digitraffic.graphql.rail.entities.PassengerInformationMessageId;
import fi.digitraffic.graphql.rail.entities.QPassengerInformationAudio;
import fi.digitraffic.graphql.rail.links.base.OneToOneLink;
import fi.digitraffic.graphql.rail.model.PassengerInformationAudioTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageTO;
import fi.digitraffic.graphql.rail.querydsl.AllFields;
import fi.digitraffic.graphql.rail.to.PassengerInformationAudioTOConverter;

@Component
public class PassengerInformationMessageToPassengerInformationAudioLink
        extends OneToOneLink<PassengerInformationMessageId, PassengerInformationMessageTO, PassengerInformationAudio, PassengerInformationAudioTO> {

    @Autowired
    private PassengerInformationAudioTOConverter audioTOConverter;

    @Override
    public String getTypeName() {
        return "PassengerInformationMessage";
    }

    @Override
    public String getFieldName() {
        return "audio";
    }

    @Override
    public PassengerInformationMessageId createKeyFromParent(final PassengerInformationMessageTO passengerInformationMessageTO) {
        return new PassengerInformationMessageId(passengerInformationMessageTO.getId(), passengerInformationMessageTO.getVersion());
    }

    @Override
    public PassengerInformationMessageId createKeyFromChild(final PassengerInformationAudioTO audioTO) {
        if (audioTO == null) {
            return null;
        }
        return new PassengerInformationMessageId(audioTO.getMessageId(), audioTO.getMessageVersion());
    }

    @Override
    public PassengerInformationAudioTO createChildTOFromTuple(final Tuple tuple) {
        return audioTOConverter.convert(tuple);
    }

    @Override
    public Class getEntityClass() {
        return PassengerInformationAudio.class;
    }

    @Override
    public Expression[] getFields() {
        return AllFields.PASSENGER_INFORMATION_MESSAGE_AUDIO;
    }

    @Override
    public EntityPath getEntityTable() {
        return QPassengerInformationAudio.passengerInformationAudio;
    }

    @Override
    public BooleanExpression createWhere(final List<PassengerInformationMessageId> keys) {
        return QPassengerInformationAudio.passengerInformationAudio.message.id.in(keys);
    }
}