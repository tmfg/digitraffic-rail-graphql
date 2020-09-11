package graphqlscope.graphql.fetchers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphqlscope.graphql.entities.Locomotive;
import graphqlscope.graphql.fetchers.base.OneToManyDataFetcher;
import graphqlscope.graphql.model.JourneySectionTO;
import graphqlscope.graphql.model.LocomotiveTO;
import graphqlscope.graphql.repositories.LocomotiveRepository;
import graphqlscope.graphql.to.LocomotiveTOConverter;

@Component
public class JourneySectionToLocomotiveDataFetcher extends OneToManyDataFetcher<Long, JourneySectionTO, Locomotive, LocomotiveTO> {
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
