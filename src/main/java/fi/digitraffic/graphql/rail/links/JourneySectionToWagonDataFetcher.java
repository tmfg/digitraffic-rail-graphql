package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Wagon;
import fi.digitraffic.graphql.rail.links.base.OneToManyDataFetcher;
import fi.digitraffic.graphql.rail.model.JourneySectionTO;
import fi.digitraffic.graphql.rail.model.WagonTO;
import fi.digitraffic.graphql.rail.repositories.WagonRepository;
import fi.digitraffic.graphql.rail.to.WagonTOConverter;

@Component
public class JourneySectionToWagonDataFetcher extends OneToManyDataFetcher<Long, JourneySectionTO, Wagon, WagonTO> {
    @Autowired
    private WagonRepository wagonRepository;

    @Autowired
    private WagonTOConverter wagonTOConverter;

    @Override
    public String getTypeName() {
        return "JourneySection";
    }

    @Override
    public String getFieldName() {
        return "wagons";
    }

    @Override
    public Long createKeyFromParent(JourneySectionTO journeySectionTO) {
        return journeySectionTO.getId().longValue();
    }

    @Override
    public Long createKeyFromChild(Wagon child) {
        return child.journeysectionId;
    }

    @Override
    public WagonTO createChildTOToFromChild(Wagon child) {
        return wagonTOConverter.convert(child);
    }

    @Override
    public List<Wagon> findChildrenByKeys(List<Long> keys) {
        return wagonRepository.findAllByJourneySectionIds(keys);
    }
}
