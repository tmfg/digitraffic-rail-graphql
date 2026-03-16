package fi.digitraffic.graphql.rail.to;

import java.util.List;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.TrainLocation;
import fi.digitraffic.graphql.rail.model.TrainLocationTO;

@Component
public class TrainLocationTOConverter extends BaseConverter {

    public TrainLocationTO convertEntity(final TrainLocation entity) {
        return new TrainLocationTO(
                entity.trainLocationId.departureDate,
                entity.speed,
                entity.accuracy,
                entity.trainLocationId.timestamp,
                (int) (long) entity.trainLocationId.trainNumber,
                List.of(entity.location.getX(), entity.location.getY()),
                null
        );
    }
}
