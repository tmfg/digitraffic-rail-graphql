package graphqlscope.graphql.to;

import org.springframework.stereotype.Component;

import graphqlscope.graphql.entities.Train;
import graphqlscope.graphql.model.TimetableTypeTO;
import graphqlscope.graphql.model.TrainTO;

@Component
public class TrainTOConverter {
    public TrainTO convertTrainToTrainTO(Train train) {
        return new TrainTO(
                train.cancelled,
                train.commuterLineID,
                train.deleted,
                train.id.departureDate,
                train.operatorShortCode,
                train.runningCurrently,
                train.timetableAcceptanceDate,
                train.timetableType.equals(Train.TimetableType.ADHOC) ? TimetableTypeTO.ADHOC : TimetableTypeTO.REGULAR,
                train.id.trainNumber.intValue(),
                train.version.toString(),
                null,
                null,
                null
        );
    }
}
