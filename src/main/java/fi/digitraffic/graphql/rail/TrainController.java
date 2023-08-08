package fi.digitraffic.graphql.rail;

import org.springframework.stereotype.Controller;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import java.util.List;

@Controller
public class TrainController {
    @SchemaMapping(typeName = "Query",value = "latestTrainLocations")
    public List<String> findAll() {
        return List.of("");
    }
}
