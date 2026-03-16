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

    @Autowired
    private CompositionFactory compositionFactory;

    @Autowired
    private JourneySectionFactory journeySectionFactory;

    @Autowired
    private RoutesetMessageFactory routesetMessageFactory;

    @Autowired
    private PassengerInformationMessageFactory passengerInformationMessageFactory;

    @Transactional
    public void deleteAll() {
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS=0").executeUpdate();

        entityManager.createQuery("DELETE FROM Station").executeUpdate();
        entityManager.createQuery("DELETE FROM TimeTableRow").executeUpdate();
        entityManager.createQuery("DELETE FROM Train").executeUpdate();
        entityManager.createQuery("DELETE FROM TrainTrackingMessage").executeUpdate();
        entityManager.createQuery("DELETE FROM TrainLocation").executeUpdate();
        entityManager.createQuery("DELETE FROM Cause").executeUpdate();
        entityManager.createQuery("DELETE FROM CategoryCode").executeUpdate();
        entityManager.createQuery("DELETE FROM JourneySection").executeUpdate();
        entityManager.createQuery("DELETE FROM Composition").executeUpdate();
        entityManager.createQuery("DELETE FROM RoutesetMessage").executeUpdate();
        entityManager.createQuery("DELETE FROM PassengerInformationMessageStation").executeUpdate();
        entityManager.createQuery("DELETE FROM PassengerInformationAudio").executeUpdate();
        entityManager.createQuery("DELETE FROM PassengerInformationVideo").executeUpdate();
        entityManager.createQuery("DELETE FROM PassengerInformationMessage").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM operator_train_number").executeUpdate();
        entityManager.createQuery("DELETE FROM Operator").executeUpdate();
        entityManager.createQuery("DELETE FROM TrainType").executeUpdate();
        entityManager.createQuery("DELETE FROM TrainCategory").executeUpdate();

        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS=1").executeUpdate();
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

    public CompositionFactory getCompositionFactory() {
        return compositionFactory;
    }

    public JourneySectionFactory getJourneySectionFactory() {
        return journeySectionFactory;
    }

    public RoutesetMessageFactory getRoutesetMessageFactory() {
        return routesetMessageFactory;
    }

    public PassengerInformationMessageFactory getPassengerInformationMessageFactory() {
        return passengerInformationMessageFactory;
    }
}
