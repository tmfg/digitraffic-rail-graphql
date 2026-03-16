package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.PassengerInformationAudio;
import fi.digitraffic.graphql.rail.entities.PassengerInformationMessageId;
import fi.digitraffic.graphql.rail.links.base.OneToOneLink;
import fi.digitraffic.graphql.rail.model.PassengerInformationAudioTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageTO;
import fi.digitraffic.graphql.rail.links.base.KeyWhereClause;
import fi.digitraffic.graphql.rail.queries.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.queries.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.PassengerInformationAudioTOConverter;

@Component
public class PassengerInformationMessageToAudioLink
        extends OneToOneLink<PassengerInformationMessageId, PassengerInformationMessageTO, PassengerInformationAudio, PassengerInformationAudioTO> {

    private final PassengerInformationAudioTOConverter audioTOConverter;

    public PassengerInformationMessageToAudioLink(final JpqlWhereBuilder jpqlWhereBuilder,
                                                  final JpqlOrderByBuilder jpqlOrderByBuilder,
                                                  @Value("${digitraffic.batch-load-size:500}") final int batchLoadSize,
                                                  final PassengerInformationAudioTOConverter audioTOConverter) {
        super(jpqlWhereBuilder, jpqlOrderByBuilder, batchLoadSize);
        this.audioTOConverter = audioTOConverter;
    }

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
    protected KeyWhereClause buildKeyWhereClause(final List<PassengerInformationMessageId> keys) {
        return simpleInClause(getEntityAlias() + ".message.id IN :keys", keys);
    }
}
