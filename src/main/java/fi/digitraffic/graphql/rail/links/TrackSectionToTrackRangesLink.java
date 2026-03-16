package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.TrackRange;
import fi.digitraffic.graphql.rail.links.base.KeyWhereClause;
import fi.digitraffic.graphql.rail.links.base.OneToManyLink;
import fi.digitraffic.graphql.rail.model.TrackRangeTO;
import fi.digitraffic.graphql.rail.model.TrackSectionTO;
import fi.digitraffic.graphql.rail.query.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.query.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.TrackRangeTOConverter;

@Component
public class TrackSectionToTrackRangesLink extends OneToManyLink<Long, TrackSectionTO, TrackRange, TrackRangeTO> {

    private final TrackRangeTOConverter trackRangeTOConverter;

    public TrackSectionToTrackRangesLink(final JpqlWhereBuilder jpqlWhereBuilder,
                                         final JpqlOrderByBuilder jpqlOrderByBuilder,
                                         @Value("${digitraffic.batch-load-size:500}") final int batchLoadSize,
                                         final TrackRangeTOConverter trackRangeTOConverter) {
        super(jpqlWhereBuilder, jpqlOrderByBuilder, batchLoadSize);
        this.trackRangeTOConverter = trackRangeTOConverter;
    }

    @Override
    public String getTypeName() { return "TrackSection"; }

    @Override
    public String getFieldName() { return "ranges"; }

    @Override
    public Long createKeyFromParent(final TrackSectionTO trackSectionTO) {
        return (long) trackSectionTO.getId();
    }

    @Override
    public Long createKeyFromChild(final TrackRangeTO trackRangeTO) {
        return (long) trackRangeTO.getTrackSectionId();
    }

    @Override
    public TrackRangeTO createChildTOFromEntity(final TrackRange entity) {
        return trackRangeTOConverter.convertEntity(entity);
    }

    @Override
    public Class<TrackRange> getEntityClass() { return TrackRange.class; }

    @Override
    protected KeyWhereClause buildKeyWhereClause(final List<Long> keys) {
        return simpleInClause(getEntityAlias() + ".trackSectionId IN :keys", keys);
    }

    @Override
    public String getDefaultOrderBy() {
        return getEntityAlias() + ".id ASC";
    }
}

