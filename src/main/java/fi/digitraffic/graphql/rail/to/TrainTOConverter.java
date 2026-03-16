package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;


import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.model.TimetableTypeTO;
import fi.digitraffic.graphql.rail.model.TrainTO;

@Component
public class TrainTOConverter extends BaseConverter {

    public TrainTO convertEntity(final Train entity) {
        return new TrainTO(
                entity.cancelled,
                entity.commuterLineid,
                entity.deleted,
                entity.id.departureDate,
                entity.operatorShortCode,
                entity.runningCurrently,
                entity.timetableAcceptanceDate,
                convert(entity.timetableType),
                entity.id.trainNumber.intValue(),
                nullableString(entity.version),
                nullableInt(entity.trainTypeId),
                nullableInt(entity.trainCategoryId),
                null, null, null, null, null, null, null, null
        );
    }

    public TimetableTypeTO convert(final Train.TimetableType timetableType) {
        if (timetableType == null) {
            return null;
        } else if (timetableType == Train.TimetableType.ADHOC) {
            return TimetableTypeTO.ADHOC;
        } else if (timetableType == Train.TimetableType.REGULAR) {
            return TimetableTypeTO.REGULAR;
        } else {
            throw new IllegalArgumentException(timetableType.toString());
        }
    }
}
