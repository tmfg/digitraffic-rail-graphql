package fi.digitraffic.graphql.rail.links.jpql;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessage;
import fi.digitraffic.graphql.rail.entities.PassengerInformationMessageId;
import fi.digitraffic.graphql.rail.links.base.jpql.OneToOneLinkJpql;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageStationTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageTO;
import fi.digitraffic.graphql.rail.querydsl.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.querydsl.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.PassengerInformationMessageTOConverter;

/**
 * JPQL implementation: PassengerInformationMessageStation → message (OneToOne).
 */
@Component
public class PassengerInformationMessageStationToMessageLink
        extends OneToOneLinkJpql<PassengerInformationMessageId, PassengerInformationMessageStationTO, PassengerInformationMessage, PassengerInformationMessageTO> {

    private final PassengerInformationMessageTOConverter messageTOConverter;

    public PassengerInformationMessageStationToMessageLink(final JpqlWhereBuilder jpqlWhereBuilder,
                                                           final JpqlOrderByBuilder jpqlOrderByBuilder,
                                                           @Value("${digitraffic.batch-load-size:500}") final int batchLoadSize,
                                                           final PassengerInformationMessageTOConverter messageTOConverter) {
        super(jpqlWhereBuilder, jpqlOrderByBuilder, batchLoadSize);
        this.messageTOConverter = messageTOConverter;
    }

    @Override
    public String getTypeName() {
        return "PassengerInformationMessageStation";
    }

    @Override
    public String getFieldName() {
        return "message";
    }

    @Override
    public PassengerInformationMessageId createKeyFromParent(final PassengerInformationMessageStationTO parent) {
        return new PassengerInformationMessageId(parent.getMessageId(), parent.getMessageVersion());
    }

    @Override
    public PassengerInformationMessageId createKeyFromChild(final PassengerInformationMessageTO child) {
        return new PassengerInformationMessageId(child.getId(), child.getVersion());
    }

    @Override
    public PassengerInformationMessageTO createChildTOFromEntity(final PassengerInformationMessage entity) {
        return messageTOConverter.convertEntity(entity);
    }

    @Override
    public Class<PassengerInformationMessage> getEntityClass() {
        return PassengerInformationMessage.class;
    }

    @Override
    public String createWhereClause(final List<PassengerInformationMessageId> keys) {
        return "e.id IN :keys";
    }
}

