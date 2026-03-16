package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.RoutesetMessage;
import fi.digitraffic.graphql.rail.entities.StringVirtualDepartureDateTrainId;
import fi.digitraffic.graphql.rail.links.base.KeyWhereClause;
import fi.digitraffic.graphql.rail.links.base.OneToManyLink;
import fi.digitraffic.graphql.rail.links.base.TrainIdWhereClause;
import fi.digitraffic.graphql.rail.model.RoutesetMessageTO;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.query.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.query.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.RoutesetMessageTOConverter;

@Component
public class TrainToRoutesetMessagesLink
        extends OneToManyLink<StringVirtualDepartureDateTrainId, TrainTO, RoutesetMessage, RoutesetMessageTO> {

    private final RoutesetMessageTOConverter routesetMessageTOConverter;

    public TrainToRoutesetMessagesLink(final JpqlWhereBuilder jpqlWhereBuilder,
                                       final JpqlOrderByBuilder jpqlOrderByBuilder,
                                       @Value("${digitraffic.batch-load-size:500}") final int batchLoadSize,
                                       final RoutesetMessageTOConverter routesetMessageTOConverter) {
        super(jpqlWhereBuilder, jpqlOrderByBuilder, batchLoadSize);
        this.routesetMessageTOConverter = routesetMessageTOConverter;
    }

    @Override
    public String getTypeName() {
        return "Train";
    }

    @Override
    public String getFieldName() {
        return "routesetMessages";
    }

    @Override
    public StringVirtualDepartureDateTrainId createKeyFromParent(final TrainTO trainTO) {
        return new StringVirtualDepartureDateTrainId(String.valueOf(trainTO.getTrainNumber()), trainTO.getDepartureDate());
    }

    @Override
    public StringVirtualDepartureDateTrainId createKeyFromChild(final RoutesetMessageTO child) {
        return new StringVirtualDepartureDateTrainId(child.getTrainNumber(), child.getDepartureDate());
    }

    @Override
    public RoutesetMessageTO createChildTOFromEntity(final RoutesetMessage entity) {
        return routesetMessageTOConverter.convertEntity(entity);
    }

    @Override
    public Class<RoutesetMessage> getEntityClass() {
        return RoutesetMessage.class;
    }

    @Override
    protected KeyWhereClause buildKeyWhereClause(final List<StringVirtualDepartureDateTrainId> keys) {
        return TrainIdWhereClause.buildForVirtualDepartureDate(
                getEntityAlias(), "trainId.virtualDepartureDate", "trainId.trainNumber", keys);
    }

    @Override
    public String getDefaultOrderBy() {
        return getEntityAlias() + ".messageTime ASC";
    }
}

