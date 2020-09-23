package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.model.TimetableTypeTO;
import fi.digitraffic.graphql.rail.model.TrainTO;

@Component
public class TrainTOConverter {
    public TrainTO convert(Train entity) {
        return new TrainTO(
                entity.cancelled,
                entity.commuterLineID,
                entity.deleted,
                entity.id.departureDate,
                entity.operatorShortCode,
                entity.runningCurrently,
                entity.timetableAcceptanceDate,
                entity.timetableType.equals(Train.TimetableType.ADHOC) ? TimetableTypeTO.ADHOC : TimetableTypeTO.REGULAR,
                entity.id.trainNumber.intValue(),
                entity.version.toString(),
                entity.trainTypeId.intValue(),
                entity.trainCategoryId.intValue(),
                null, null, null, null, null, null, null
        );
    }
}
