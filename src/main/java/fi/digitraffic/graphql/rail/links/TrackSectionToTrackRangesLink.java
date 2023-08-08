package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import fi.digitraffic.graphql.rail.entities.QTrackRange;
import fi.digitraffic.graphql.rail.entities.TrackRange;
import fi.digitraffic.graphql.rail.links.base.OneToManyLink;
import fi.digitraffic.graphql.rail.model.TrackRangeTO;
import fi.digitraffic.graphql.rail.model.TrackSectionTO;
import fi.digitraffic.graphql.rail.querydsl.AllFields;
import fi.digitraffic.graphql.rail.to.TrackRangeTOConverter;

@Component
public class TrackSectionToTrackRangesLink extends OneToManyLink<Long, TrackSectionTO, TrackRange, TrackRangeTO> {
    @Autowired
    private TrackRangeTOConverter trackRangeTOConverter;

    @Override
    public String getTypeName() {
        return "TrackSection";
    }

    @Override
    public String getFieldName() {
        return "ranges";
    }

    @Override
    public Long createKeyFromParent(final TrackSectionTO trackSectionTO) {
        return Long.valueOf(trackSectionTO.getId());
    }

    @Override
    public Long createKeyFromChild(final TrackRangeTO trackRangeTO) {
        return Long.valueOf(trackRangeTO.getTrackSectionId());
    }

    @Override
    public TrackRangeTO createChildTOFromTuple(final Tuple tuple) {
        return trackRangeTOConverter.convert(tuple);
    }

    @Override
    public Class<TrackRange> getEntityClass() {
        return TrackRange.class;
    }

    @Override
    public Expression[] getFields() {
        return AllFields.TRACK_RANGE;
    }

    @Override
    public EntityPath getEntityTable() {
        return QTrackRange.trackRange;
    }

    @Override
    public BooleanExpression createWhere(final List<Long> keys) {
        return QTrackRange.trackRange.trackSectionId.in(keys);
    }

    @Override
    public OrderSpecifier createDefaultOrder() {
        return new OrderSpecifier(Order.ASC, QTrackRange.trackRange.id);
    }
}
