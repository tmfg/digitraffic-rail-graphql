package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import fi.digitraffic.graphql.rail.entities.Operator;
import fi.digitraffic.graphql.rail.entities.QOperator;
import fi.digitraffic.graphql.rail.model.OperatorTO;

@Component
public class OperatorTOConverter extends BaseConverter<OperatorTO> {
    @Override
    public OperatorTO convert(final Tuple tuple) {
        return new OperatorTO(
                tuple.get(QOperator.operator.name),
                tuple.get(QOperator.operator.shortCode),
                tuple.get(QOperator.operator.operatorUicCode).intValue()
        );
    }

    /**
     * Converts an Operator entity to OperatorTO.
     * Used by JPQL-based queries and links.
     */
    public OperatorTO convertEntity(final Operator entity) {
        return new OperatorTO(
                entity.name,
                entity.shortCode,
                entity.operatorUicCode
        );
    }
}
