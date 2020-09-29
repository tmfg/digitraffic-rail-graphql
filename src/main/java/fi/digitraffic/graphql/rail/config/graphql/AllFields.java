package fi.digitraffic.graphql.rail.config.graphql;

import com.querydsl.core.types.Expression;
import fi.digitraffic.graphql.rail.entities.QComposition;
import fi.digitraffic.graphql.rail.entities.QRouteset;
import fi.digitraffic.graphql.rail.entities.QTrain;
import fi.digitraffic.graphql.rail.entities.QTrainLocation;
import fi.digitraffic.graphql.rail.entities.QTrainTrackingMessage;

public class AllFields {
    public static Expression[] TRAIN = new Expression[]{
            QTrain.train.cancelled,
            QTrain.train.commuterLineid,
            QTrain.train.deleted,
            QTrain.train.operatorShortCode,
            QTrain.train.operatorUicCode,
            QTrain.train.runningCurrently,
            QTrain.train.timetableAcceptanceDate,
            QTrain.train.timetableType,
            QTrain.train.trainCategoryId,
            QTrain.train.trainTypeId,
            QTrain.train.version,
            QTrain.train.id.trainNumber,
            QTrain.train.id.departureDate};

    public static Expression[] COMPOSITION = new Expression[]{
            QComposition.composition.version,
            QComposition.composition.operatorShortCode,
            QComposition.composition.operatorUicCode,
            QComposition.composition.trainCategoryId,
            QComposition.composition.trainTypeId,
            QComposition.composition.id.departureDate,
            QComposition.composition.id.trainNumber,
    };

    public static Expression[] TRAIN_LOCATION = new Expression[]{
            QTrainLocation.trainLocation.id,
            QTrainLocation.trainLocation.connectionQuality,
            QTrainLocation.trainLocation.location,
            QTrainLocation.trainLocation.speed,
            QTrainLocation.trainLocation.trainLocationId.departureDate,
            QTrainLocation.trainLocation.trainLocationId.timestamp,
            QTrainLocation.trainLocation.trainLocationId.trainNumber
    };

    public static Expression[] ROUTESET = new Expression[]{
            QRouteset.routeset.clientSystem,
            QRouteset.routeset.departureDate,
            QRouteset.routeset.id,
            QRouteset.routeset.messageId,
            QRouteset.routeset.messageTime,
            QRouteset.routeset.routeType,
            QRouteset.routeset.version,
            QRouteset.routeset.trainId.trainNumber,
            QRouteset.routeset.trainId.virtualDepartureDate
    };

    public static Expression[] TRAIN_TRACKING_MESSAGE = new Expression[]{
            QTrainTrackingMessage.trainTrackingMessage.version,
            QTrainTrackingMessage.trainTrackingMessage.timestamp,
            QTrainTrackingMessage.trainTrackingMessage.type,
            QTrainTrackingMessage.trainTrackingMessage.departureDate,
            QTrainTrackingMessage.trainTrackingMessage.trainId.trainNumber,
            QTrainTrackingMessage.trainTrackingMessage.trainId.virtualDepartureDate,
            QTrainTrackingMessage.trainTrackingMessage.id,
            QTrainTrackingMessage.trainTrackingMessage.nextStation,
            QTrainTrackingMessage.trainTrackingMessage.previousStation,
            QTrainTrackingMessage.trainTrackingMessage.station,
            QTrainTrackingMessage.trainTrackingMessage.nextTrackSection,
            QTrainTrackingMessage.trainTrackingMessage.previousTrackSection,
            QTrainTrackingMessage.trainTrackingMessage.trackSection
    };
}
