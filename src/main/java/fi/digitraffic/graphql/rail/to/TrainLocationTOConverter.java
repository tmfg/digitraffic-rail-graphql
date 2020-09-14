package fi.digitraffic.graphql.rail.to;

import java.util.List;

import org.springframework.stereotype.Component;

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
                List.of((float) entity.location.getX(), (float) entity.location.getY())
        );
    }
}
