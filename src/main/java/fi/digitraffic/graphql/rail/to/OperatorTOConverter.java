package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Operator;
import fi.digitraffic.graphql.rail.model.OperatorTO;

@Component
public class OperatorTOConverter {
    public OperatorTO convert(Operator entity) {
        return new OperatorTO(
                entity.name, entity.shortCode, entity.operatorUicCode);
    }
}
