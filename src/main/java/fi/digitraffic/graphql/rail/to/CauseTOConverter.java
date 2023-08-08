package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import fi.digitraffic.graphql.rail.entities.QCause;
import fi.digitraffic.graphql.rail.model.CauseTO;

@Component
public class CauseTOConverter extends BaseConverter<CauseTO> {
    @Override
    public CauseTO convert(Tuple tuple) {
        return new CauseTO(
                tuple.get(QCause.cause.timeTableRowId.attapId).longValue(),
                tuple.get(QCause.cause.timeTableRowId.trainNumber).longValue(),
                tuple.get(QCause.cause.timeTableRowId.departureDate),
                tuple.get(QCause.cause.id).longValue(),
                tuple.get(QCause.cause.categoryCodeOid),
                tuple.get(QCause.cause.detailedCategoryCodeOid),
                tuple.get(QCause.cause.thirdCategoryCodeOid),
                null,
                null,
                null
        );
    }
}
