package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Composition;
import fi.digitraffic.graphql.rail.model.CompositionTO;

@Component
public class CompositionTOConverter {
    public CompositionTO convert(Composition entity) {
        return new CompositionTO(
                entity.id.departureDate,
                entity.id.trainNumber.intValue(),
                entity.operatorShortCode,
                entity.trainCategoryId.intValue(),
                entity.trainTypeId.intValue(),
                entity.version.toString(),
                entity.operatorUicCode,
                null,
                null
        );
    }
}
