package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import fi.digitraffic.graphql.rail.entities.QJourneySection;
import fi.digitraffic.graphql.rail.model.JourneySectionTO;

@Component
public class JourneySectionTOConverter extends BaseConverter<JourneySectionTO> {
    @Override
    public JourneySectionTO convert(Tuple tuple) {
        return new JourneySectionTO(
                tuple.get(QJourneySection.journeySection.id).intValue(),
                tuple.get(QJourneySection.journeySection.trainId.departureDate),
                tuple.get(QJourneySection.journeySection.trainId.trainNumber).intValue(),
                nullableInt(tuple.get(QJourneySection.journeySection.attapId)),
                nullableInt(tuple.get(QJourneySection.journeySection.saapAttapId)),
                tuple.get(QJourneySection.journeySection.maximumSpeed),
                tuple.get(QJourneySection.journeySection.totalLength),
                null,
                null,
                null,
                null
        );
    }
}
