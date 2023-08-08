package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import fi.digitraffic.graphql.rail.entities.QLocomotive;
import fi.digitraffic.graphql.rail.model.LocomotiveTO;

@Component
public class LocomotiveTOConverter extends BaseConverter<LocomotiveTO> {
    public LocomotiveTO convert(Tuple tuple) {
        return new LocomotiveTO(
                tuple.get(QLocomotive.locomotive.id).longValue(),
                tuple.get(QLocomotive.locomotive.location),
                tuple.get(QLocomotive.locomotive.locomotiveType),
                tuple.get(QLocomotive.locomotive.powerTypeAbbreviation),
                tuple.get(QLocomotive.locomotive.journeysectionId).longValue(),
                tuple.get(QLocomotive.locomotive.vehicleNumber)
        );
    }
}
