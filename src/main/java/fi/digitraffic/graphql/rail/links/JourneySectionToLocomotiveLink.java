package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Locomotive;
import fi.digitraffic.graphql.rail.links.base.OneToManyLink;
import fi.digitraffic.graphql.rail.model.JourneySectionTO;
import fi.digitraffic.graphql.rail.model.LocomotiveTO;
import fi.digitraffic.graphql.rail.repositories.LocomotiveRepository;
import fi.digitraffic.graphql.rail.to.LocomotiveTOConverter;

@Component
public class JourneySectionToLocomotiveLink extends OneToManyLink<Long, JourneySectionTO, Locomotive, LocomotiveTO> {
    @Autowired
    private LocomotiveRepository locomotiveRepository;

    @Autowired
    private LocomotiveTOConverter locomotiveTOConverter;

    @Override
    public String getTypeName() {
        return "JourneySection";
    }

    @Override
    public String getFieldName() {
        return "locomotives";
    }

    @Override
    public Long createKeyFromParent(JourneySectionTO journeySectionTO) {
        return journeySectionTO.getId().longValue();
    }

    @Override
    public Long createKeyFromChild(Locomotive child) {
        return child.journeysectionId;
    }

    @Override
    public LocomotiveTO createChildTOToFromChild(Locomotive child) {
        return locomotiveTOConverter.convert(child);
    }

    @Override
    public List<Locomotive> findChildrenByKeys(List<Long> keys) {
        return locomotiveRepository.findAllByJourneySectionIds(keys);
    }
}
