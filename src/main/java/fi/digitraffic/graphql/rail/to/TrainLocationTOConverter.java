package fi.digitraffic.graphql.rail.to;

import java.util.List;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import fi.digitraffic.graphql.rail.entities.QTrainLocation;
import fi.digitraffic.graphql.rail.entities.TrainLocation;
import fi.digitraffic.graphql.rail.model.TrainLocationTO;

@Component
public class TrainLocationTOConverter {
    public TrainLocationTO convert(TrainLocation entity) {
        return new TrainLocationTO(
                entity.trainLocationId.departureDate,
                entity.speed,
                entity.trainLocationId.timestamp,
                entity.trainLocationId.trainNumber.intValue(),
                List.of((float) entity.location.getX(), (float) entity.location.getY()),
                null
        );
    }

    public TrainLocationTO convert(Tuple tuple) {
        return new TrainLocationTO(
                tuple.get(QTrainLocation.trainLocation.trainLocationId.departureDate),
                tuple.get(QTrainLocation.trainLocation.speed),
                tuple.get(QTrainLocation.trainLocation.trainLocationId.timestamp),
                tuple.get(QTrainLocation.trainLocation.trainLocationId.trainNumber).intValue(),
                List.of((float) tuple.get(QTrainLocation.trainLocation.location).getX(), (float) tuple.get(QTrainLocation.trainLocation.location).getY()),
                null
        );
    }
}
