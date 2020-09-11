package graphqlscope.graphql.to;

import org.springframework.stereotype.Component;

import graphqlscope.graphql.entities.Operator;
import graphqlscope.graphql.model.OperatorTO;

@Component
public class OperatorTOConverter {
    public OperatorTO convert(Operator entity) {
        return new OperatorTO(
                entity.operatorName, entity.operatorShortCode, entity.operatorUicCode);
    }
}
