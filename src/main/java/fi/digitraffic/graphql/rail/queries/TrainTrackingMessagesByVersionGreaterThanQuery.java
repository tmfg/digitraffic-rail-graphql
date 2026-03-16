package fi.digitraffic.graphql.rail.queries;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.TrainTrackingMessage;
import fi.digitraffic.graphql.rail.model.TrainTrackingMessageTO;
import fi.digitraffic.graphql.rail.to.TrainTrackingTOConverter;
import graphql.schema.DataFetchingEnvironment;

@Component
public class TrainTrackingMessagesByVersionGreaterThanQuery extends BaseQuery<TrainTrackingMessage, TrainTrackingMessageTO> {

    private static final int MAX_LIMIT = 2000;

    private final TrainTrackingTOConverter trainTrackingTOConverter;

    public TrainTrackingMessagesByVersionGreaterThanQuery(final JpqlWhereBuilder whereBuilder,
                                                          final JpqlOrderByBuilder orderByBuilder,
                                                          @Value("${digitraffic.max-returned-rows}") final int maxResults,
                                                          final TrainTrackingTOConverter trainTrackingTOConverter) {
        super(whereBuilder, orderByBuilder, Math.min(maxResults, MAX_LIMIT));
        this.trainTrackingTOConverter = trainTrackingTOConverter;
    }

    @Override
    public String getQueryName() {
        return "trainTrackingMessagesByVersionGreaterThan";
    }

    @Override
    public Class<TrainTrackingMessage> getEntityClass() {
        return TrainTrackingMessage.class;
    }

    @Override
    public String buildBaseWhereClause(final String alias, final DataFetchingEnvironment env,
                                       final Map<String, Object> parameters) {
        final Long version = Long.parseLong(env.getArgument("version"));
        parameters.put("version", version);
        return alias + ".version > :version";
    }

    @Override
    public String getDefaultOrderBy(final String alias) {
        return alias + ".version ASC";
    }

    @Override
    public TrainTrackingMessageTO convertEntityToTO(final TrainTrackingMessage entity) {
        return trainTrackingTOConverter.convertEntity(entity);
    }
}

