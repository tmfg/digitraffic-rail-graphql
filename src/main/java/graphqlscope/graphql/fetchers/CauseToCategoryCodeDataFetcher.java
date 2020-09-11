package graphqlscope.graphql.fetchers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphqlscope.graphql.entities.CategoryCode;
import graphqlscope.graphql.fetchers.base.OneToOneDataFetcher;
import graphqlscope.graphql.model.CategoryCodeTO;
import graphqlscope.graphql.model.CauseTO;
import graphqlscope.graphql.repositories.CategoryCodeRepository;
import graphqlscope.graphql.to.CategoryCodeTOConverter;

@Component
public class CauseToCategoryCodeDataFetcher extends OneToOneDataFetcher<Long, CauseTO, CategoryCode, CategoryCodeTO> {
    @Autowired
    private CategoryCodeRepository categoryCodeRepository;

    @Autowired
    private CategoryCodeTOConverter categoryCodeTOConverter;

    @Override
    public String getTypeName() {
        return "Cause";
    }

    @Override
    public String getFieldName() {
        return "categoryCode";
    }

    @Override
    public Long createKeyFromParent(CauseTO causeTO) {
        return causeTO.getCategoryCodeId().longValue();
    }

    @Override
    public Long createKeyFromChild(CategoryCode child) {
        return child.id;
    }

    @Override
    public CategoryCodeTO createChildTOToFromChild(CategoryCode child) {
        return categoryCodeTOConverter.convert(child);
    }

    @Override
    public List<CategoryCode> findChildrenByKeys(List<Long> keys) {
        return categoryCodeRepository.findAllById(keys);
    }

}
