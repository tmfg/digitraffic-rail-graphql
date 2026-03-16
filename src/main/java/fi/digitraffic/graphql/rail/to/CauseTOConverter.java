package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import fi.digitraffic.graphql.rail.entities.Cause;
import fi.digitraffic.graphql.rail.entities.QCause;
import fi.digitraffic.graphql.rail.model.CauseTO;

@Component
public class CauseTOConverter extends BaseConverter<CauseTO> {
    @Override
    public CauseTO convert(Tuple tuple) {
        return new CauseTO(
                tuple.get(QCause.cause.timeTableRowId.attapId).intValue(),
                tuple.get(QCause.cause.timeTableRowId.trainNumber).intValue(),
                tuple.get(QCause.cause.timeTableRowId.departureDate),
                tuple.get(QCause.cause.id).intValue(),
                tuple.get(QCause.cause.categoryCodeOid),
                tuple.get(QCause.cause.detailedCategoryCodeOid),
                tuple.get(QCause.cause.thirdCategoryCodeOid),
                null,
                null,
                null
        );
    }

    public CauseTO convertEntity(final Cause entity) {
        return new CauseTO(
                entity.timeTableRowId.attapId.intValue(),
                entity.timeTableRowId.trainNumber.intValue(),
                entity.timeTableRowId.departureDate,
                entity.id.intValue(),
                entity.categoryCodeOid,
                entity.detailedCategoryCodeOid,
                entity.thirdCategoryCodeOid,
                null,
                null,
                null
        );
    }
}
