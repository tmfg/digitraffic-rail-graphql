package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import fi.digitraffic.graphql.rail.entities.QWagon;
import fi.digitraffic.graphql.rail.model.WagonTO;

@Component
public class WagonTOConverter extends BaseConverter<WagonTO> {
    @Override
    public WagonTO convert(final Tuple tuple) {
        return new WagonTO(
                tuple.get(QWagon.wagon.id).intValue(),
                tuple.get(QWagon.wagon.length),
                tuple.get(QWagon.wagon.location),
                tuple.get(QWagon.wagon.salesNumber),
                tuple.get(QWagon.wagon.journeysectionId).intValue(),
                tuple.get(QWagon.wagon.catering),
                tuple.get(QWagon.wagon.disabled),
                tuple.get(QWagon.wagon.luggage),
                tuple.get(QWagon.wagon.pet),
                tuple.get(QWagon.wagon.playground),
                tuple.get(QWagon.wagon.smoking),
                tuple.get(QWagon.wagon.video),
                tuple.get(QWagon.wagon.wagonType),
                tuple.get(QWagon.wagon.vehicleNumber)
        );
    }
}
