package fi.digitraffic.graphql.rail.links.jpql;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessageId;
import fi.digitraffic.graphql.rail.entities.PassengerInformationMessageStation;
import fi.digitraffic.graphql.rail.links.base.jpql.OneToManyLinkJpql;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageStationTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageTO;
import fi.digitraffic.graphql.rail.querydsl.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.querydsl.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.PassengerInformationMessageStationTOConverter;

/**
 * JPQL implementation: PassengerInformationMessage → messageStations (OneToMany).
 */
@Component
public class PassengerInformationMessageToMessageStationLink
        extends OneToManyLinkJpql<PassengerInformationMessageId, PassengerInformationMessageTO, PassengerInformationMessageStation, PassengerInformationMessageStationTO> {

    private final PassengerInformationMessageStationTOConverter stationTOConverter;

    public PassengerInformationMessageToMessageStationLink(final JpqlWhereBuilder jpqlWhereBuilder,
                                                           final JpqlOrderByBuilder jpqlOrderByBuilder,
                                                           @Value("${digitraffic.batch-load-size:500}") final int batchLoadSize,
                                                           final PassengerInformationMessageStationTOConverter stationTOConverter) {
        super(jpqlWhereBuilder, jpqlOrderByBuilder, batchLoadSize);
        this.stationTOConverter = stationTOConverter;
    }

    @Override
    public String getTypeName() {
        return "PassengerInformationMessage";
    }

    @Override
    public String getFieldName() {
        return "messageStations";
    }

    @Override
    public PassengerInformationMessageId createKeyFromParent(final PassengerInformationMessageTO parent) {
        return new PassengerInformationMessageId(parent.getId(), parent.getVersion());
    }

    @Override
    public PassengerInformationMessageId createKeyFromChild(final PassengerInformationMessageStationTO child) {
        return new PassengerInformationMessageId(child.getMessageId(), child.getMessageVersion());
    }

    @Override
    public PassengerInformationMessageStationTO createChildTOFromEntity(final PassengerInformationMessageStation entity) {
        return stationTOConverter.convertEntity(entity);
    }

    @Override
    public Class<PassengerInformationMessageStation> getEntityClass() {
        return PassengerInformationMessageStation.class;
    }

    @Override
    public String createWhereClause(final List<PassengerInformationMessageId> keys) {
        return "e.message.id IN :keys";
    }
}

