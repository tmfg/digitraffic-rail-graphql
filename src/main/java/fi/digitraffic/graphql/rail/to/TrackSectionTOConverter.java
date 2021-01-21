package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import fi.digitraffic.graphql.rail.entities.QTrackSection;
import fi.digitraffic.graphql.rail.model.TrackSectionTO;

@Component
public class TrackSectionTOConverter {
    public TrackSectionTO convert(Tuple tuple) {
        return new TrackSectionTO(
                tuple.get(QTrackSection.trackSection.trackSectionCode),
                null);
    }
}
