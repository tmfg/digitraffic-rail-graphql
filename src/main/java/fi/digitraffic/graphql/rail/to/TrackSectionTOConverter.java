package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.TrackSection;
import fi.digitraffic.graphql.rail.model.TrackSectionTO;

@Component
public class TrackSectionTOConverter {

    public TrackSectionTO convertEntity(final TrackSection entity) {
        return new TrackSectionTO(
                entity.id.intValue(),
                entity.trackSectionCode,
                entity.stationShortCode,
                null, null);
    }
}
