package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.TrackRange;
import fi.digitraffic.graphql.rail.model.TrackRangeTO;

@Component
public class TrackRangeTOConverter {

    public TrackRangeTO convertEntity(final TrackRange entity) {
        return new TrackRangeTO(
                entity.trackSectionId.intValue(),
                entity.startTrack,
                entity.startKilometres,
                entity.startMetres,
                entity.endTrack,
                entity.endKilometres,
                entity.endMetres);
    }
}
