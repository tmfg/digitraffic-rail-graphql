package fi.digitraffic.graphql.rail.filters;

import java.util.HashMap;
import java.util.Map;

public class BooleanFilter extends BaseFilter {

    public boolean filter(HashMap<String, Object> entityEntry, Map.Entry<String, Object> filterEntry) {
        Boolean value = (Boolean) entityEntry.get(filterEntry.getKey());
        Map<String, Boolean> filterValue = (Map<String, Boolean>) filterEntry.getValue();
        if (value != filterValue.get("eq")) {
            return true;
        } else {
            return false;
        }
    }
}
