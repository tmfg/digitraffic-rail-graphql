package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.JourneySection;
import fi.digitraffic.graphql.rail.model.JourneySectionTO;

@Component
public class JourneySectionTOConverter {
    public JourneySectionTO convert(JourneySection entity) {
        return new JourneySectionTO(
                entity.id.intValue(),
                entity.trainId.departureDate,
                entity.trainId.trainNumber.intValue(),
                entity.attapId.intValue(),
                entity.saapAttapId.intValue(),
                entity.maximumSpeed,
                entity.totalLength,
                null,
                null,
                null,
                null
        );
    }
}
