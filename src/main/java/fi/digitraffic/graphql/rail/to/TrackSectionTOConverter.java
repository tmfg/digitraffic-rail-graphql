package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import fi.digitraffic.graphql.rail.entities.QTrackSection;
import fi.digitraffic.graphql.rail.entities.TrackSection;
import fi.digitraffic.graphql.rail.model.TrackSectionTO;

@Component
public class TrackSectionTOConverter {
    public TrackSectionTO convert(final Tuple tuple) {
        return new TrackSectionTO(
                tuple.get(QTrackSection.trackSection.id).intValue(),
                tuple.get(QTrackSection.trackSection.trackSectionCode),
                tuple.get(QTrackSection.trackSection.stationShortCode),
                null, null);
    }

    public TrackSectionTO convertEntity(final TrackSection entity) {
        return new TrackSectionTO(
                entity.id.intValue(),
                entity.trackSectionCode,
                entity.stationShortCode,
                null, null);
    }
}
