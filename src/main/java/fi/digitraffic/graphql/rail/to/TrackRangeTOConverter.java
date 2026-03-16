package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import fi.digitraffic.graphql.rail.entities.QTrackRange;
import fi.digitraffic.graphql.rail.entities.TrackRange;
import fi.digitraffic.graphql.rail.model.TrackRangeTO;

@Component
public class TrackRangeTOConverter {
    public TrackRangeTO convert(final Tuple tuple) {
        return new TrackRangeTO(
                tuple.get(QTrackRange.trackRange.trackSectionId).intValue(),
                tuple.get(QTrackRange.trackRange.startTrack),
                tuple.get(QTrackRange.trackRange.startKilometres),
                tuple.get(QTrackRange.trackRange.startMetres),
                tuple.get(QTrackRange.trackRange.endTrack),
                tuple.get(QTrackRange.trackRange.endKilometres),
                tuple.get(QTrackRange.trackRange.endMetres));
    }

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
