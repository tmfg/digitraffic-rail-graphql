package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Locomotive;
import fi.digitraffic.graphql.rail.model.LocomotiveTO;

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
