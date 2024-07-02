package fi.digitraffic.graphql.rail.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class FactoryService {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private StationFactory stationFactory;

    @Autowired
    private TimeTableRowFactory timeTableRowFactory;

    @Autowired
    private TrainFactory trainFactory;

    @Autowired
    private TrainTrackingMessageFactory trainTrackingMessageFactory;

    @Autowired
    private TrainLocationFactory trainLocationFactory;

    @Autowired
    private CauseFactory causeFactory;

    @Transactional
    public void deleteAll() {
        entityManager.createQuery("DELETE FROM Station").executeUpdate();
        entityManager.createQuery("DELETE FROM TimeTableRow").executeUpdate();
        entityManager.createQuery("DELETE FROM Train").executeUpdate();
        entityManager.createQuery("DELETE FROM TrainTrackingMessage").executeUpdate();
        entityManager.createQuery("DELETE FROM TrainLocation").executeUpdate();
        entityManager.createQuery("DELETE FROM Cause").executeUpdate();
    }

    public StationFactory getStationFactory() {
        return stationFactory;
    }

    public TimeTableRowFactory getTimeTableRowFactory() {
        return timeTableRowFactory;
    }

    public TrainFactory getTrainFactory() {
        return trainFactory;
    }

    public TrainTrackingMessageFactory getTrainTrackingMessageFactory() {
        return trainTrackingMessageFactory;
    }

    public TrainLocationFactory getTrainLocationFactory() {
        return trainLocationFactory;
    }

    public CauseFactory getCauseFactory() {
        return causeFactory;
    }
}
