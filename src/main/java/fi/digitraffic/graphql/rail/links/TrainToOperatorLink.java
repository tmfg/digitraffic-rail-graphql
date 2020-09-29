package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import fi.digitraffic.graphql.rail.config.graphql.AllFields;
import fi.digitraffic.graphql.rail.entities.Operator;
import fi.digitraffic.graphql.rail.entities.QOperator;
import fi.digitraffic.graphql.rail.links.base.OneToOneLink;
import fi.digitraffic.graphql.rail.model.OperatorTO;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.to.OperatorTOConverter;

@Component
public class TrainToOperatorLink extends OneToOneLink<String, TrainTO, Operator, OperatorTO> {
    @Autowired
    private OperatorTOConverter operatorTOConverter;

    @Override
    public String getTypeName() {
        return "Train";
    }

    @Override
    public String getFieldName() {
        return "operator";
    }

    @Override
    public String createKeyFromParent(TrainTO trainTO) {
        return trainTO.getOperatorShortCode();
    }

    @Override
    public String createKeyFromChild(OperatorTO operatorTO) {
        return operatorTO.getShortCode();
    }

    @Override
    public OperatorTO createChildTOFromTuple(Tuple tuple) {
        return operatorTOConverter.convert(tuple);
    }

    @Override
    public Class getEntityClass() {
        return Operator.class;
    }

    @Override
    public Expression[] getFields() {
        return AllFields.OPERATOR;
    }

    @Override
    public EntityPath getEntityTable() {
        return QOperator.operator;
    }

    @Override
    public BooleanExpression createWhere(List<String> keys) {
        return QOperator.operator.shortCode.in(keys);
    }
}
