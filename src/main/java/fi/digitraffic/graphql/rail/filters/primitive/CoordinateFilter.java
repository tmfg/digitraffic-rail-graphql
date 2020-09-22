package fi.digitraffic.graphql.rail.filters.primitive;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import fi.digitraffic.graphql.rail.filters.BaseFilter;
import fi.digitraffic.graphql.rail.model.CoordinateFilterTO;

@Component
public class CoordinateFilter extends BaseFilter<List<Float>, CoordinateFilterTO> {

    @Override
    public boolean isFiltered(List<Float> value, CoordinateFilterTO filterTO) {
        List<Float> coordinates = Lists.newArrayList(filterTO.getInside());
        Pair<Float, Float> coordinate1 = Pair.of(coordinates.get(0), coordinates.get(1));
        Pair<Float, Float> coordinate2 = Pair.of(coordinates.get(2), coordinates.get(3));

        if (value.get(0) >= coordinate1.getLeft() &&
                value.get(1) >= coordinate1.getRight() &&
                value.get(0) <= coordinate2.getLeft() &&
                value.get(1) <= coordinate2.getRight()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Class getFilterTOType() {
        return CoordinateFilterTO.class;
    }

    @Override
    public Class getEntityTOType() {
        return List.class;
    }
}
