package fi.digitraffic.graphql.rail.querydsl;

import com.querydsl.core.types.Expression;

import fi.digitraffic.graphql.rail.entities.QCategoryCode;
import fi.digitraffic.graphql.rail.entities.QCause;
import fi.digitraffic.graphql.rail.entities.QComposition;
import fi.digitraffic.graphql.rail.entities.QDetailedCategoryCode;
import fi.digitraffic.graphql.rail.entities.QJourneySection;
import fi.digitraffic.graphql.rail.entities.QLocomotive;
import fi.digitraffic.graphql.rail.entities.QOperator;
import fi.digitraffic.graphql.rail.entities.QPassengerInformationMessage;
import fi.digitraffic.graphql.rail.entities.QRoutesection;
import fi.digitraffic.graphql.rail.entities.QRoutesetMessage;
import fi.digitraffic.graphql.rail.entities.QStation;
import fi.digitraffic.graphql.rail.entities.QThirdCategoryCode;
import fi.digitraffic.graphql.rail.entities.QTimeTableRow;
import fi.digitraffic.graphql.rail.entities.QTrackRange;
import fi.digitraffic.graphql.rail.entities.QTrackSection;
import fi.digitraffic.graphql.rail.entities.QTrain;
import fi.digitraffic.graphql.rail.entities.QTrainCategory;
import fi.digitraffic.graphql.rail.entities.QTrainLocation;
import fi.digitraffic.graphql.rail.entities.QTrainTrackingMessage;
import fi.digitraffic.graphql.rail.entities.QTrainType;
import fi.digitraffic.graphql.rail.entities.QWagon;

public class AllFields {
    public static final Expression[] OPERATOR = new Expression[] {
            QOperator.operator.shortCode,
            QOperator.operator.name,
            QOperator.operator.id,
            QOperator.operator.operatorUicCode
    };
    public static final Expression[] ROUTESECTION = new Expression[] {
            QRoutesection.routesection.sectionOrder,
            QRoutesection.routesection.routesetId,
            QRoutesection.routesection.id,
            QRoutesection.routesection.commercialTrackId,
            QRoutesection.routesection.stationCode,
            QRoutesection.routesection.sectionId
    };
    public static final Expression[] WAGON = new Expression[] {
            QWagon.wagon.catering,
            QWagon.wagon.disabled,
            QWagon.wagon.id,
            QWagon.wagon.journeysectionId,
            QWagon.wagon.length,
            QWagon.wagon.location,
            QWagon.wagon.luggage,
            QWagon.wagon.pet,
            QWagon.wagon.playground,
            QWagon.wagon.salesNumber,
            QWagon.wagon.smoking,
            QWagon.wagon.vehicleNumber,
            QWagon.wagon.video,
            QWagon.wagon.wagonType
    };
    public static final Expression[] LOCOMOTIVE = new Expression[] {
            QLocomotive.locomotive.id,
            QLocomotive.locomotive.journeysectionId,
            QLocomotive.locomotive.location,
            QLocomotive.locomotive.locomotiveType,
            QLocomotive.locomotive.powerTypeAbbreviation,
            QLocomotive.locomotive.vehicleNumber
    };
    public static final Expression[] JOURNEY_SECTION = new Expression[] {
            QJourneySection.journeySection.id,
            QJourneySection.journeySection.attapId,
            QJourneySection.journeySection.maximumSpeed,
            QJourneySection.journeySection.saapAttapId,
            QJourneySection.journeySection.totalLength,
            QJourneySection.journeySection.trainId.trainNumber,
            QJourneySection.journeySection.trainId.departureDate
    };
    public static final Expression[] THIRD_CATEGORY_CODE = new Expression[] {
            QThirdCategoryCode.thirdCategoryCode.oid,
            QThirdCategoryCode.thirdCategoryCode.detailedCategoryCodeOid,
            QThirdCategoryCode.thirdCategoryCode.code,
            QThirdCategoryCode.thirdCategoryCode.name,
            QThirdCategoryCode.thirdCategoryCode.description,
            QThirdCategoryCode.thirdCategoryCode.validFrom,
            QThirdCategoryCode.thirdCategoryCode.validTo
    };
    public static final Expression[] DETAILED_CATEGORY_CODE = new Expression[] {
            QDetailedCategoryCode.detailedCategoryCode.oid,
            QDetailedCategoryCode.detailedCategoryCode.categoryCodeOid,
            QDetailedCategoryCode.detailedCategoryCode.code,
            QDetailedCategoryCode.detailedCategoryCode.name,
            QDetailedCategoryCode.detailedCategoryCode.validFrom,
            QDetailedCategoryCode.detailedCategoryCode.validTo
    };
    public static Expression[] CATEGORY_CODE = new Expression[] {
            QCategoryCode.categoryCode.oid,
            QCategoryCode.categoryCode.code,
            QCategoryCode.categoryCode.name,
            QCategoryCode.categoryCode.validFrom,
            QCategoryCode.categoryCode.validTo
    };

    public static Expression[] CAUSE = new Expression[] {
            QCause.cause.id,
            QCause.cause.timeTableRowId.trainNumber,
            QCause.cause.timeTableRowId.departureDate,
            QCause.cause.timeTableRowId.attapId,
            QCause.cause.categoryCodeOid,
            QCause.cause.detailedCategoryCodeOid,
            QCause.cause.thirdCategoryCodeOid
    };

    public static Expression[] TRAIN_TYPE = new Expression[] {
            QTrainType.trainType.id,
            QTrainType.trainType.name,
            QTrainType.trainType.trainCategoryId
    };

    public static Expression[] TRAIN_CATEGORY = new Expression[] {
            QTrainCategory.trainCategory.id,
            QTrainCategory.trainCategory.name
    };
    public static Expression[] TRAIN = new Expression[] {
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
            QTrain.train.id.departureDate };

    public static Expression[] COMPOSITION = new Expression[] {
            QComposition.composition.version,
            QComposition.composition.operatorShortCode,
            QComposition.composition.operatorUicCode,
            QComposition.composition.trainCategoryId,
            QComposition.composition.trainTypeId,
            QComposition.composition.id.departureDate,
            QComposition.composition.id.trainNumber,
            };

    public static Expression[] TRAIN_LOCATION = new Expression[] {
            QTrainLocation.trainLocation.id,
            QTrainLocation.trainLocation.location,
            QTrainLocation.trainLocation.speed,
            QTrainLocation.trainLocation.accuracy,
            QTrainLocation.trainLocation.trainLocationId.departureDate,
            QTrainLocation.trainLocation.trainLocationId.timestamp,
            QTrainLocation.trainLocation.trainLocationId.trainNumber
    };

    public static Expression[] ROUTESET = new Expression[] {
            QRoutesetMessage.routesetMessage.clientSystem,
            QRoutesetMessage.routesetMessage.departureDate,
            QRoutesetMessage.routesetMessage.id,
            QRoutesetMessage.routesetMessage.messageId,
            QRoutesetMessage.routesetMessage.messageTime,
            QRoutesetMessage.routesetMessage.routeType,
            QRoutesetMessage.routesetMessage.version,
            QRoutesetMessage.routesetMessage.trainId.trainNumber,
            QRoutesetMessage.routesetMessage.trainId.virtualDepartureDate
    };

    public static Expression[] TRAIN_TRACKING_MESSAGE = new Expression[] {
            QTrainTrackingMessage.trainTrackingMessage.version,
            QTrainTrackingMessage.trainTrackingMessage.timestamp,
            QTrainTrackingMessage.trainTrackingMessage.type,
            QTrainTrackingMessage.trainTrackingMessage.departureDate,
            QTrainTrackingMessage.trainTrackingMessage.trainId.trainNumber,
            QTrainTrackingMessage.trainTrackingMessage.trainId.virtualDepartureDate,
            QTrainTrackingMessage.trainTrackingMessage.id,
            QTrainTrackingMessage.trainTrackingMessage.nextStationShortCode,
            QTrainTrackingMessage.trainTrackingMessage.previousStationShortCode,
            QTrainTrackingMessage.trainTrackingMessage.stationShortCode,
            QTrainTrackingMessage.trainTrackingMessage.nextStationShortCode,
            QTrainTrackingMessage.trainTrackingMessage.previousTrackSectionCode,
            QTrainTrackingMessage.trainTrackingMessage.track_section
    };

    public static Expression[] TIME_TABLE_ROW = new Expression[] {
            QTimeTableRow.timeTableRow.actualTime,
            QTimeTableRow.timeTableRow.cancelled,
            QTimeTableRow.timeTableRow.commercialStop,
            QTimeTableRow.timeTableRow.commercialTrack,
            QTimeTableRow.timeTableRow.id.attapId,
            QTimeTableRow.timeTableRow.id.departureDate,
            QTimeTableRow.timeTableRow.id.trainNumber,
            QTimeTableRow.timeTableRow.countryCode,
            QTimeTableRow.timeTableRow.differenceInMinutes,
            QTimeTableRow.timeTableRow.estimateSource,
            QTimeTableRow.timeTableRow.liveEstimateTime,
            QTimeTableRow.timeTableRow.scheduledTime,
            QTimeTableRow.timeTableRow.stationShortCode,
            QTimeTableRow.timeTableRow.stationUICCode,
            QTimeTableRow.timeTableRow.trainStopping,
            QTimeTableRow.timeTableRow.type,
            QTimeTableRow.timeTableRow.unknownDelay
    };

    public static Expression[] STATION = new Expression[] {
            QStation.station.shortCode,
            QStation.station.countryCode,
            QStation.station.id,
            QStation.station.latitude,
            QStation.station.longitude,
            QStation.station.name,
            QStation.station.passengerTraffic,
            QStation.station.type,
            QStation.station.uicCode
    };

    public static Expression[] TRACK_SECTION = new Expression[] {
            QTrackSection.trackSection.trackSectionCode,
            QTrackSection.trackSection.id,
            };

    public static Expression[] TRACK_RANGE = new Expression[] {
            QTrackRange.trackRange.trackSectionId,
            QTrackRange.trackRange.startTrack,
            QTrackRange.trackRange.startKilometres,
            QTrackRange.trackRange.startMetres,
            QTrackRange.trackRange.endTrack,
            QTrackRange.trackRange.endKilometres,
            QTrackRange.trackRange.endMetres
    };

    public static final Expression[] PASSENGER_INFORMATION_MESSAGE = new Expression[] {
            QPassengerInformationMessage.passengerInformationMessage.id,
            QPassengerInformationMessage.passengerInformationMessage.version,
            QPassengerInformationMessage.passengerInformationMessage.creationDateTime,
            QPassengerInformationMessage.passengerInformationMessage.startValidity,
            QPassengerInformationMessage.passengerInformationMessage.endValidity,
            QPassengerInformationMessage.passengerInformationMessage.trainNumber,
            QPassengerInformationMessage.passengerInformationMessage.trainDepartureDate,
            QPassengerInformationMessage.passengerInformationMessage.stations,
            QPassengerInformationMessage.passengerInformationMessage.audio,
            QPassengerInformationMessage.passengerInformationMessage.video
    };
}
