package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;

import fi.digitraffic.graphql.rail.entities.QTrain;
import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.model.TimetableTypeTO;
import fi.digitraffic.graphql.rail.model.TrainTO;

@Component
public class TrainTOConverter extends BaseConverter<TrainTO> {
    public TrainTO convert(final Tuple row) {
        return new TrainTO(
                row.get(QTrain.train.cancelled),
                row.get(QTrain.train.commuterLineid),
                row.get(QTrain.train.deleted),
                row.get(QTrain.train.id.departureDate),
                row.get(QTrain.train.operatorShortCode),
                row.get(QTrain.train.runningCurrently),
                row.get(QTrain.train.timetableAcceptanceDate),
                convert(row.get(QTrain.train.timetableType)),
                row.get(QTrain.train.id.trainNumber).intValue(),
                nullableString(row.get(QTrain.train.version)),
                nullableInt(row.get(QTrain.train.trainTypeId)),
                nullableInt(row.get(QTrain.train.trainCategoryId)),

                null, null, null, null, null, null, null, null
        );
    }

    /**
     * Converts a Train entity to TrainTO.
     * Used by JPQL-based queries and links.
     */
    public TrainTO convertEntity(final Train entity) {
        return new TrainTO(
                entity.cancelled,
                entity.commuterLineid,
                entity.deleted,
                entity.id.departureDate,
                entity.operatorShortCode,
                entity.runningCurrently,
                entity.timetableAcceptanceDate,
                convert(entity.timetableType),
                entity.id.trainNumber.intValue(),
                nullableString(entity.version),
                nullableInt(entity.trainTypeId),
                nullableInt(entity.trainCategoryId),
                null, null, null, null, null, null, null, null
        );
    }

    public TimetableTypeTO convert(final Train.TimetableType timetableType) {
        if (timetableType == null) {
            return null;
        } else if (timetableType == Train.TimetableType.ADHOC) {
            return TimetableTypeTO.ADHOC;
        } else if (timetableType == Train.TimetableType.REGULAR) {
            return TimetableTypeTO.REGULAR;
        } else {
            throw new IllegalArgumentException(timetableType.toString());
        }
    }
}
