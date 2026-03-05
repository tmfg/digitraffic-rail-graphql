package fi.digitraffic.graphql.rail.links.jpql;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.PassengerInformationAudio;
import fi.digitraffic.graphql.rail.entities.PassengerInformationMessageId;
import fi.digitraffic.graphql.rail.links.base.jpql.OneToOneLinkJpql;
import fi.digitraffic.graphql.rail.model.PassengerInformationAudioTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageTO;
import fi.digitraffic.graphql.rail.to.PassengerInformationAudioTOConverter;

/**
 * JPQL implementation: PassengerInformationMessage → audio (OneToOne).
 */
@Component
public class PassengerInformationMessageToAudioLink
        extends OneToOneLinkJpql<PassengerInformationMessageId, PassengerInformationMessageTO, PassengerInformationAudio, PassengerInformationAudioTO> {

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
    public PassengerInformationMessageId createKeyFromParent(final PassengerInformationMessageTO parent) {
        return new PassengerInformationMessageId(parent.getId(), parent.getVersion());
    }

    @Override
    public PassengerInformationMessageId createKeyFromChild(final PassengerInformationAudioTO child) {
        if (child == null) {
            return null;
        }
        return new PassengerInformationMessageId(child.getMessageId(), child.getMessageVersion());
    }

    @Override
    public PassengerInformationAudioTO createChildTOFromEntity(final PassengerInformationAudio entity) {
        return audioTOConverter.convertEntity(entity);
    }

    @Override
    public Class<PassengerInformationAudio> getEntityClass() {
        return PassengerInformationAudio.class;
    }

    @Override
    public String createWhereClause(final List<PassengerInformationMessageId> keys) {
        return "e.message.id IN :keys";
    }
}

