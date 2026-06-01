package fi.digitraffic.graphql.rail.links;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessage;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.links.base.KeyWhereClause;
import fi.digitraffic.graphql.rail.links.base.OneToManyLink;
import fi.digitraffic.graphql.rail.links.base.TrainIdWhereClause;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageTO;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.queries.PassengerInformationMessagesQuery;
import fi.digitraffic.graphql.rail.queries.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.queries.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.PassengerInformationMessageTOConverter;
import jakarta.persistence.Tuple;

@Component
public class TrainToPassengerInformationMessagesLink
        extends OneToManyLink<TrainId, TrainTO, PassengerInformationMessage, PassengerInformationMessageTO> {

    private final PassengerInformationMessageTOConverter passengerInformationMessageTOConverter;

    public TrainToPassengerInformationMessagesLink(final JpqlWhereBuilder jpqlWhereBuilder,
                                                   final JpqlOrderByBuilder jpqlOrderByBuilder,
                                                   @Value("${digitraffic.batch-load-size:500}") final int batchLoadSize,
                                                   final PassengerInformationMessageTOConverter passengerInformationMessageTOConverter) {
        super(jpqlWhereBuilder, jpqlOrderByBuilder, batchLoadSize);
        this.passengerInformationMessageTOConverter = passengerInformationMessageTOConverter;
    }

    @Override
    public String getTypeName() {
        return "Train";
    }

    @Override
    public String getFieldName() {
        return "passengerInformationMessages";
    }

    @Override
    public TrainId createKeyFromParent(final TrainTO trainTO) {
        return new TrainId(trainTO.getTrainNumber(), trainTO.getDepartureDate());
    }

    @Override
    public TrainId createKeyFromChild(final PassengerInformationMessageTO child) {
        if (child == null) {
            return null;
        }
        return new TrainId(child.getTrainNumber(), child.getTrainDepartureDate());
    }

    @Override
    public PassengerInformationMessageTO createChildTOFromEntity(final PassengerInformationMessage entity) {
        return passengerInformationMessageTOConverter.convertEntity(entity);
    }

    @Override
    protected String getProjectionExpression() {
        return "e.id.id AS id, e.id.version AS version, e.creationDateTime AS creationDateTime, " +
                "e.startValidity AS startValidity, e.endValidity AS endValidity, e.trainDepartureDate AS trainDepartureDate, e.trainNumber AS trainNumber";
    }

    @Override
    protected PassengerInformationMessageTO createChildTOFromProjection(final Tuple row) {
        return passengerInformationMessageTOConverter.convertProjection(row);
    }

    @Override
    public Class<PassengerInformationMessage> getEntityClass() {
        return PassengerInformationMessage.class;
    }

    @Override
    protected KeyWhereClause buildKeyWhereClause(final List<TrainId> keys) {
        return TrainIdWhereClause.build(getEntityAlias(), "trainDepartureDate", "trainNumber", keys);
    }

    @Override
    protected KeyWhereClause buildBaseWhereClause() {
        final String alias = getEntityAlias();
        final ZonedDateTime now = ZonedDateTime.now();
        return new KeyWhereClause(
                PassengerInformationMessagesQuery.latestVersionSubquery(alias)
                + " AND " + PassengerInformationMessagesQuery.activeMessageCondition(alias),
                Map.of("now", now));
    }

    @Override
    public String getDefaultOrderBy() {
        return getEntityAlias() + ".creationDateTime ASC";
    }
}



