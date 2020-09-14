package fi.digitraffic.graphql.rail.fetchers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.ThirdCategoryCode;
import fi.digitraffic.graphql.rail.fetchers.base.OneToOneDataFetcher;
import fi.digitraffic.graphql.rail.model.CauseTO;
import fi.digitraffic.graphql.rail.model.ThirdCategoryCodeTO;
import fi.digitraffic.graphql.rail.repositories.ThirdCategoryCodeRepository;
import fi.digitraffic.graphql.rail.to.ThirdCategoryCodeTOConverter;

@Component
public class CauseToThirdCategoryCodeDataFetcher extends OneToOneDataFetcher<Long, CauseTO, ThirdCategoryCode, ThirdCategoryCodeTO> {
    @Autowired
    private ThirdCategoryCodeRepository thirdCategoryCodeRepository;

    @Autowired
    private ThirdCategoryCodeTOConverter thirdCategoryCodeTOConverter;

    @Override
    public String getTypeName() {
        return "Cause";
    }

    @Override
    public String getFieldName() {
        return "thirdCategoryCode";
    }

    @Override
    public Long createKeyFromParent(CauseTO causeTO) {
        Integer thirdCategoryCodeId = causeTO.getThirdCategoryCodeId();
        if (thirdCategoryCodeId == null) {
            return -1L;
        } else {
            return thirdCategoryCodeId.longValue();
        }
    }

    @Override
    public Long createKeyFromChild(ThirdCategoryCode child) {
        return child.id;
    }

    @Override
    public ThirdCategoryCodeTO createChildTOToFromChild(ThirdCategoryCode child) {
        return thirdCategoryCodeTOConverter.convert(child);
    }

    @Override
    public List<ThirdCategoryCode> findChildrenByKeys(List<Long> keys) {
        return thirdCategoryCodeRepository.findAllById(keys);
    }

}
