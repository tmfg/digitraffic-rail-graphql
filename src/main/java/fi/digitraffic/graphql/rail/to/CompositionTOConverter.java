package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import fi.digitraffic.graphql.rail.entities.QComposition;
import fi.digitraffic.graphql.rail.model.CompositionTO;

@Component
public class CompositionTOConverter {
    public CompositionTO convert(Tuple tuple) {
        return new CompositionTO(
                tuple.get(QComposition.composition.id.departureDate),
                tuple.get(QComposition.composition.id.trainNumber).intValue(),
                tuple.get(QComposition.composition.operatorShortCode),
                tuple.get(QComposition.composition.trainCategoryId).intValue(),
                tuple.get(QComposition.composition.trainTypeId).intValue(),
                tuple.get(QComposition.composition.version).toString(),
                tuple.get(QComposition.composition.operatorUicCode),
                null,
                null
        );
    }
}
