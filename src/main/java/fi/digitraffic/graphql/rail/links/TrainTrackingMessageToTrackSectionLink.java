package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import fi.digitraffic.graphql.rail.entities.QTrackSection;
import fi.digitraffic.graphql.rail.entities.QTrainTrackingMessage;
import fi.digitraffic.graphql.rail.entities.TrackSection;
import fi.digitraffic.graphql.rail.links.base.OneToOneLink;
import fi.digitraffic.graphql.rail.model.TrackSectionTO;
import fi.digitraffic.graphql.rail.model.TrainTrackingMessageTO;
import fi.digitraffic.graphql.rail.to.TrackSectionTOConverter;

@Component
public class TrainTrackingMessageToTrackSectionLink extends OneToOneLink<String, TrainTrackingMessageTO, TrackSection, TrackSectionTO> {
    @Autowired
    private TrackSectionTOConverter trackSectionTOConverter;

    @Override
    public String getTypeName() {
        return "TrainTrackingMessage";
    }

    @Override
    public String getFieldName() {
        return "trackSection";
    }

    @Override
    public String createKeyFromParent(TrainTrackingMessageTO trainTrackingMessageTO) {
        return Strings.nullToEmpty(trainTrackingMessageTO.getTrackSectionCode());
    }

    @Override
    public String createKeyFromChild(TrackSectionTO trackSectionTO) {
        return trackSectionTO.getTrackSectionCode();
    }

    @Override
    public TrackSectionTO createChildTOFromTuple(Tuple tuple) {
        return trackSectionTOConverter.convert(tuple);
    }

    @Override
    public Class getEntityClass() {
        return TrackSection.class;
    }

    @Override
    public EntityPath getEntityTable() {
        return QTrackSection.trackSection;
    }

    @Override
    public BooleanExpression createWhere(List<String> keys) {
        return QTrackSection.trackSection.trackSectionCode.in(keys);
    }

    @Override
    public List<Expression<?>> columnsNeededFromParentTable() {
        return List.of(QTrainTrackingMessage.trainTrackingMessage.track_section);
    }
}
