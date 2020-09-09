package graphqlscope.graphql.to;

import org.springframework.stereotype.Component;

import graphqlscope.graphql.entities.Train;
import graphqlscope.graphql.model.TimetableTypeTO;
import graphqlscope.graphql.model.TrainTO;

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
                null,
                null,
                null
        );
    }
}
