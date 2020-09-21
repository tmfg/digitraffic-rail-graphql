package fi.digitraffic.graphql.rail.factory;

import java.time.ZonedDateTime;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.entities.TrainLocation;
import fi.digitraffic.graphql.rail.entities.TrainLocationConnectionQuality;
import fi.digitraffic.graphql.rail.entities.TrainLocationId;
import fi.digitraffic.graphql.rail.repositories.TrainLocationRepository;

@Component
public class TrainLocationFactory {
    private GeometryFactory geometryFactory = new GeometryFactory();

    @Autowired
    private TrainLocationRepository trainLocationRepository;

    @Transactional
    public TrainLocation create(double x, double y, Integer speed, Train train) {
        final TrainLocation trainLocation = new TrainLocation();
        trainLocation.trainLocationId = new TrainLocationId(train.id.trainNumber, train.id.departureDate, ZonedDateTime.now());
        trainLocation.speed = speed;
        trainLocation.location = geometryFactory.createPoint(new Coordinate(x, y));
        trainLocation.connectionQuality = TrainLocationConnectionQuality.OK;

        return trainLocationRepository.save(trainLocation);
    }
}
