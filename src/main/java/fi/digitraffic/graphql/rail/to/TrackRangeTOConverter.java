package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import fi.digitraffic.graphql.rail.entities.QTrackRange;
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
}
