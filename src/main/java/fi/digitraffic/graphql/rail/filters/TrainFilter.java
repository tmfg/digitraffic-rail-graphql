package fi.digitraffic.graphql.rail.filters;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.model.TimeTableRowFilterTO;
import fi.digitraffic.graphql.rail.model.TrainFilterTO;
import fi.digitraffic.graphql.rail.model.TrainTO;

@Component
public class TrainFilter extends BaseFilter<TrainTO, TrainFilterTO> {
    List<List<Method>> fields = new ArrayList<>();

    @PostConstruct
    public void setup() throws NoSuchMethodException {
        List<Method> getMethods = List.of(getFilterTOType().getDeclaredMethods()).stream().filter(s -> s.getName().startsWith("get")).collect(Collectors.toList());
        for (Method getMethod : getMethods) {
            String methodName = getMethod.getName();
            if (methodName.equals("getAnd") || methodName.equals("getOr")) {

            } else if (getMethod.getReturnType().getName().endsWith("CollectionFilter")) {

            } else {
                fields.add(List.of(getEntityTOType().getDeclaredMethod(methodName), getFilterTOType().getDeclaredMethod(methodName)));
            }
        }
    }

    public boolean isFiltered(TrainTO entity, TrainFilterTO filter) {
        if (this.filterByAnd(filter.getAnd(), entity)) return true;
        if (this.filterByOr(filter.getOr(), entity)) return true;

        try {
            for (List<Method> methods : fields) {
                Object entityFieldValue = methods.get(0).invoke(entity);
                Object filterFieldValue = methods.get(1).invoke(filter);
                if (this.isChildFiltered(entityFieldValue, filterFieldValue)) return true;
            }
        } catch (Exception e) {

        }

        if (this.filterCollection(entity.getTimeTableRows(), filter.getTimeTableRows())) return true;

        return false;
    }

    private Boolean filterCollection(Collection entities, TimeTableRowFilterTO timeTableRowCollectionFilter) {
        if (timeTableRowCollectionFilter != null) {
//            FilteringLogicTO logic = timeTableRowCollectionFilter.getLogic();
//            if (logic == FilteringLogicTO.ALL_ROWS_MATCH) {
//                for (Object entity : entities) {
//                    boolean result = isChildFiltered(entity, timeTableRowCollectionFilter.getFilter());
//
//                    if (result) {
//                        return true;
//                    }
//                }
//
//                return false;
//            } else if (logic == FilteringLogicTO.ATLEAST_ONE_ROW_MATCHES) {
            for (Object entity : entities) {
                boolean result = isChildFiltered(entity, timeTableRowCollectionFilter);

                if (!result) {
                    return false;
                }
            }

            return true;
//            }
        }
        return false;
    }

    public Class<TrainFilterTO> getFilterTOType() {
        return TrainFilterTO.class;
    }

    public Class<TrainTO> getEntityTOType() {
        return TrainTO.class;
    }
}
