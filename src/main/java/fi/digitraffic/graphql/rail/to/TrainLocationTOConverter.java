package fi.digitraffic.graphql.rail.to;

import java.util.List;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import fi.digitraffic.graphql.rail.entities.QTrainLocation;
import fi.digitraffic.graphql.rail.model.TrainLocationTO;

@Component
public class TrainLocationTOConverter extends BaseConverter<TrainLocationTO> {
    public TrainLocationTO convert(final Tuple tuple) {
        return new TrainLocationTO(
                tuple.get(QTrainLocation.trainLocation.trainLocationId.departureDate),
                tuple.get(QTrainLocation.trainLocation.speed),
                tuple.get(QTrainLocation.trainLocation.accuracy),
                tuple.get(QTrainLocation.trainLocation.trainLocationId.timestamp),
                nullableInt(tuple.get(QTrainLocation.trainLocation.trainLocationId.trainNumber)),
                List.of(tuple.get(QTrainLocation.trainLocation.location).getX(), tuple.get(QTrainLocation.trainLocation.location).getY()),
                null
        );
    }
}
