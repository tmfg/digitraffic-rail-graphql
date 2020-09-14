package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Wagon;
import fi.digitraffic.graphql.rail.model.WagonTO;

@Component
public class WagonTOConverter {
    public WagonTO convert(Wagon entity) {
        return new WagonTO(
                entity.id.intValue(),
                entity.length,
                entity.location,
                entity.salesNumber,
                entity.journeysectionId.intValue(),
                entity.catering,
                entity.disabled,
                entity.luggage,
                entity.pet,
                entity.playground,
                entity.smoking,
                entity.video,
                entity.wagonType,
                entity.vehicleNumber
        );
    }
}
