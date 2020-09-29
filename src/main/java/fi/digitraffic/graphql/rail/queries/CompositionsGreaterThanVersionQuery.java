package fi.digitraffic.graphql.rail.queries;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import fi.digitraffic.graphql.rail.config.graphql.AllFields;
import fi.digitraffic.graphql.rail.entities.Composition;
import fi.digitraffic.graphql.rail.entities.QComposition;
import fi.digitraffic.graphql.rail.model.CompositionTO;
import fi.digitraffic.graphql.rail.to.CompositionTOConverter;
import graphql.schema.DataFetchingEnvironment;

@Component
public class CompositionsGreaterThanVersionQuery extends BaseQuery<CompositionTO> {
    @Autowired
    private CompositionTOConverter compositionTOConverter;

    @Override
    public String getQueryName() {
        return "compositionsGreaterThanVersion";
    }

    @Override
    public Class getEntityClass() {
        return Composition.class;
    }

    @Override
    public Expression[] getFields() {
        return AllFields.COMPOSITION;
    }

    @Override
    public EntityPath getEntityTable() {
        return QComposition.composition;
    }

    @Override
    public BooleanExpression createWhereFromArguments(DataFetchingEnvironment dataFetchingEnvironment) {
        Long version = Long.parseLong(dataFetchingEnvironment.getArgument("version"));
        return QComposition.composition.version.gt(version);
    }

    @Override
    public CompositionTO convertEntityToTO(Tuple tuple) {
        return compositionTOConverter.convert(tuple);
    }

    @Override
    public OrderSpecifier createDefaultOrder() {
        return new OrderSpecifier(Order.ASC, QComposition.composition.version);
    }
}
