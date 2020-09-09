package graphqlscope.graphql.to;

import org.springframework.stereotype.Component;

import graphqlscope.graphql.entities.Locomotive;
import graphqlscope.graphql.model.LocomotiveTO;

@Component
public class LocomotiveTOConverter {
    public LocomotiveTO convert(Locomotive entity) {
        return new LocomotiveTO(
                entity.id.intValue(),
                entity.location,
                entity.locomotiveType,
                entity.powerTypeAbbreviation,
                entity.journeysectionId.intValue(),
                entity.vehicleNumber
        );
    }
}
