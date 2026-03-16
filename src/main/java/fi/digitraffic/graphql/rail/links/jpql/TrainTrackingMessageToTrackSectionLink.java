package fi.digitraffic.graphql.rail.links.jpql;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

import fi.digitraffic.graphql.rail.entities.TrackSection;
import fi.digitraffic.graphql.rail.links.base.jpql.KeyWhereClause;
import fi.digitraffic.graphql.rail.links.base.jpql.OneToOneLinkJpql;
import fi.digitraffic.graphql.rail.model.TrackSectionTO;
import fi.digitraffic.graphql.rail.model.TrainTrackingMessageTO;
import fi.digitraffic.graphql.rail.querydsl.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.querydsl.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.TrackSectionTOConverter;

@Component
public class TrainTrackingMessageToTrackSectionLink extends OneToOneLinkJpql<String, TrainTrackingMessageTO, TrackSection, TrackSectionTO> {

    private final TrackSectionTOConverter trackSectionTOConverter;

    public TrainTrackingMessageToTrackSectionLink(final JpqlWhereBuilder jpqlWhereBuilder,
                                                  final JpqlOrderByBuilder jpqlOrderByBuilder,
                                                  @Value("${digitraffic.batch-load-size:500}") final int batchLoadSize,
                                                  final TrackSectionTOConverter trackSectionTOConverter) {
        super(jpqlWhereBuilder, jpqlOrderByBuilder, batchLoadSize);
        this.trackSectionTOConverter = trackSectionTOConverter;
    }

    @Override
    public String getTypeName() { return "TrainTrackingMessage"; }

    @Override
    public String getFieldName() { return "trackSection"; }

    @Override
    public String createKeyFromParent(final TrainTrackingMessageTO msg) {
        return Strings.nullToEmpty(msg.getTrackSectionCode());
    }

    @Override
    public String createKeyFromChild(final TrackSectionTO trackSectionTO) {
        return trackSectionTO.getTrackSectionCode();
    }

    @Override
    public TrackSectionTO createChildTOFromEntity(final TrackSection entity) {
        return trackSectionTOConverter.convertEntity(entity);
    }

    @Override
    public Class<TrackSection> getEntityClass() { return TrackSection.class; }

    @Override
    protected KeyWhereClause buildKeyWhereClause(final List<String> keys) {
        return simpleInClause(getEntityAlias() + ".trackSectionCode IN :keys", keys);
    }
}

