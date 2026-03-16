package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessageId;
import fi.digitraffic.graphql.rail.entities.PassengerInformationVideo;
import fi.digitraffic.graphql.rail.links.base.OneToOneLink;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationVideoTO;
import fi.digitraffic.graphql.rail.links.base.KeyWhereClause;
import fi.digitraffic.graphql.rail.query.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.query.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.PassengerInformationVideoTOConverter;

@Component
public class PassengerInformationMessageToVideoLink
        extends OneToOneLink<PassengerInformationMessageId, PassengerInformationMessageTO, PassengerInformationVideo, PassengerInformationVideoTO> {

    private final PassengerInformationVideoTOConverter videoTOConverter;

    public PassengerInformationMessageToVideoLink(final JpqlWhereBuilder jpqlWhereBuilder,
                                                  final JpqlOrderByBuilder jpqlOrderByBuilder,
                                                  @Value("${digitraffic.batch-load-size:500}") final int batchLoadSize,
                                                  final PassengerInformationVideoTOConverter videoTOConverter) {
        super(jpqlWhereBuilder, jpqlOrderByBuilder, batchLoadSize);
        this.videoTOConverter = videoTOConverter;
    }

    @Override
    public String getTypeName() {
        return "PassengerInformationMessage";
    }

    @Override
    public String getFieldName() {
        return "video";
    }

    @Override
    public PassengerInformationMessageId createKeyFromParent(final PassengerInformationMessageTO parent) {
        return new PassengerInformationMessageId(parent.getId(), parent.getVersion());
    }

    @Override
    public PassengerInformationMessageId createKeyFromChild(final PassengerInformationVideoTO child) {
        if (child == null) {
            return null;
        }
        return new PassengerInformationMessageId(child.getMessageId(), child.getMessageVersion());
    }

    @Override
    public PassengerInformationVideoTO createChildTOFromEntity(final PassengerInformationVideo entity) {
        return videoTOConverter.convertEntity(entity);
    }

    @Override
    public Class<PassengerInformationVideo> getEntityClass() {
        return PassengerInformationVideo.class;
    }

    @Override
    protected KeyWhereClause buildKeyWhereClause(final List<PassengerInformationMessageId> keys) {
        return simpleInClause(getEntityAlias() + ".message.id IN :keys", keys);
    }
}
