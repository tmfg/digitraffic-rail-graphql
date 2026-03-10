package fi.digitraffic.graphql.rail.links.jpql;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessageId;
import fi.digitraffic.graphql.rail.entities.PassengerInformationVideo;
import fi.digitraffic.graphql.rail.links.base.jpql.OneToOneLinkJpql;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationVideoTO;
import fi.digitraffic.graphql.rail.querydsl.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.querydsl.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.PassengerInformationVideoTOConverter;

/**
 * JPQL implementation: PassengerInformationMessage → video (OneToOne).
 */
@Component
public class PassengerInformationMessageToVideoLink
        extends OneToOneLinkJpql<PassengerInformationMessageId, PassengerInformationMessageTO, PassengerInformationVideo, PassengerInformationVideoTO> {

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
    public String createWhereClause(final List<PassengerInformationMessageId> keys) {
        return getEntityAlias() + ".message.id IN :keys";
    }
}

