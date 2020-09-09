package graphqlscope.graphql.to;

import org.springframework.stereotype.Component;

import graphqlscope.graphql.entities.JourneySection;
import graphqlscope.graphql.model.JourneySectionTO;

@Component
public class JourneySectionTOConverter {
    public JourneySectionTO convert(JourneySection entity) {
        return new JourneySectionTO(
                entity.id.intValue(),
                entity.trainId.departureDate,
                entity.trainId.trainNumber.intValue(),
                entity.beginTimeTableRowId.intValue(),
                entity.endTimeTableRowId.intValue(),
                entity.maximumSpeed,
                entity.totalLength
        );
    }
}
