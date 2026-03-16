package fi.digitraffic.graphql.rail.factory;


import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.CategoryCode;
import fi.digitraffic.graphql.rail.entities.Cause;
import fi.digitraffic.graphql.rail.entities.TimeTableRow;
import fi.digitraffic.graphql.rail.repositories.CategoryCodeRepository;
import fi.digitraffic.graphql.rail.repositories.CauseRepository;

@Component
public class CauseFactory {
    @Autowired
    private CauseRepository causeRepository;

    @Autowired
    private CategoryCodeRepository categoryCodeRepository;

    public Cause create(TimeTableRow timeTableRow) {
        CategoryCode categoryCode = new CategoryCode();
        categoryCode.code = "A";
        categoryCode.oid = "B";
        categoryCode.name= "C";
        categoryCode.validFrom = LocalDate.now().minusYears(2);
        categoryCode.validTo = LocalDate.now().plusYears(2);
        categoryCodeRepository.save(categoryCode);

        Cause cause = new Cause();
        cause.timeTableRowId= timeTableRow.id;
        cause.id=1L;
        cause.categoryCodeOid = categoryCode.oid;

        return causeRepository.save(cause);
    }
}
