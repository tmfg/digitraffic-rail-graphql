package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import fi.digitraffic.graphql.rail.entities.QTrackRange;
import fi.digitraffic.graphql.rail.model.TrackRangeTO;

@Component
public class TrackRangeTOConverter extends BaseConverter<TrackRangeTO> {
    public TrackRangeTO convert(final Tuple tuple) {
        return new TrackRangeTO(
                tuple.get(QTrackRange.trackRange.trackSectionId).intValue(),
                tuple.get(QTrackRange.trackRange.startTrack),
                zeroIfNull(tuple.get(QTrackRange.trackRange.startKilometres)),
                zeroIfNull(tuple.get(QTrackRange.trackRange.startMetres)),
                tuple.get(QTrackRange.trackRange.endTrack),
                zeroIfNull(tuple.get(QTrackRange.trackRange.endKilometres)),
                zeroIfNull(tuple.get(QTrackRange.trackRange.endMetres)));
    }
}
