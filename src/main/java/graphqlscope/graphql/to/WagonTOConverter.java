package graphqlscope.graphql.to;

import org.springframework.stereotype.Component;

import graphqlscope.graphql.entities.Wagon;
import graphqlscope.graphql.model.WagonTO;

@Component
public class WagonTOConverter {
    public WagonTO convert(Wagon entity) {
        return new WagonTO(
                entity.id.intValue(),
                entity.catering,
                entity.disabled,
                entity.length,
                entity.location,
                entity.luggage,
                entity.pet,
                entity.playground,
                entity.salesNumber,
                entity.smoking,
                entity.video,
                entity.wagonType,
                entity.vehicleNumber,
                entity.journeysectionId.intValue()
        );
    }
}
