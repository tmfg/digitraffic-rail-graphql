package graphqlscope.graphql.to;

import java.util.List;

import org.springframework.stereotype.Component;

import graphqlscope.graphql.entities.TrainLocation;
import graphqlscope.graphql.model.TrainLocationTO;

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
