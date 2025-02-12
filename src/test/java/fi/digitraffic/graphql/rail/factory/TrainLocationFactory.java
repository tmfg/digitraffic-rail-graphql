package fi.digitraffic.graphql.rail.factory;

import java.time.ZonedDateTime;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.entities.TrainLocation;
import fi.digitraffic.graphql.rail.entities.TrainLocationId;
import fi.digitraffic.graphql.rail.repositories.TrainLocationRepository;

@Component
public class TrainLocationFactory {
    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Autowired
    private TrainLocationRepository trainLocationRepository;

    @Transactional
    public TrainLocation create(final double x, final double y, final Integer speed, final Train train) {
        final TrainLocation trainLocation = new TrainLocation();
        trainLocation.trainLocationId = new TrainLocationId(train.id.trainNumber, train.id.departureDate, ZonedDateTime.now());
        trainLocation.speed = speed;
        trainLocation.location = geometryFactory.createPoint(new Coordinate(x, y));

        return trainLocationRepository.save(trainLocation);
    }
}
