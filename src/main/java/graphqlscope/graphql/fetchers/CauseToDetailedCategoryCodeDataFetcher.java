package graphqlscope.graphql.fetchers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphqlscope.graphql.entities.DetailedCategoryCode;
import graphqlscope.graphql.fetchers.base.OneToOneDataFetcher;
import graphqlscope.graphql.model.CauseTO;
import graphqlscope.graphql.model.DetailedCategoryCodeTO;
import graphqlscope.graphql.repositories.DetailedCategoryCodeRepository;
import graphqlscope.graphql.to.DetailedCategoryCodeTOConverter;

@Component
public class CauseToDetailedCategoryCodeDataFetcher extends OneToOneDataFetcher<Long, CauseTO, DetailedCategoryCode, DetailedCategoryCodeTO> {
    @Autowired
    private DetailedCategoryCodeRepository detailedCategoryCodeRepository;

    @Autowired
    private DetailedCategoryCodeTOConverter detailedCategoryCodeTOConverter;

    @Override
    public String getTypeName() {
        return "Cause";
    }

    @Override
    public String getFieldName() {
        return "detailedCategoryCode";
    }

    @Override
    public Long createKeyFromParent(CauseTO causeTO) {
        Integer detailedCategoryCodeId = causeTO.getDetailedCategoryCodeId();
        if (detailedCategoryCodeId == null) {
            return -1L;
        } else {
            return detailedCategoryCodeId.longValue();
        }
    }

    @Override
    public Long createKeyFromChild(DetailedCategoryCode child) {
        return child.id;
    }

    @Override
    public DetailedCategoryCodeTO createChildTOToFromChild(DetailedCategoryCode child) {
        return detailedCategoryCodeTOConverter.convert(child);
    }

    @Override
    public List<DetailedCategoryCode> findChildrenByKeys(List<Long> keys) {
        return detailedCategoryCodeRepository.findAllById(keys);
    }

}
