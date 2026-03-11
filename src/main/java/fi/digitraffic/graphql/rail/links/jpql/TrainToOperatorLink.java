package fi.digitraffic.graphql.rail.links.jpql;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Operator;
import fi.digitraffic.graphql.rail.links.base.jpql.OneToOneLinkJpql;
import fi.digitraffic.graphql.rail.model.OperatorTO;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.querydsl.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.querydsl.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.OperatorTOConverter;

/**
 * JPQL implementation of TrainToOperatorLink.
 * Links Train to its Operator via operatorShortCode.
 */
@Component
public class TrainToOperatorLink extends OneToOneLinkJpql<String, TrainTO, Operator, OperatorTO> {

    private final OperatorTOConverter operatorTOConverter;

    public TrainToOperatorLink(final JpqlWhereBuilder jpqlWhereBuilder,
                               final JpqlOrderByBuilder jpqlOrderByBuilder,
                               @Value("${digitraffic.batch-load-size:500}") final int batchLoadSize,
                               final OperatorTOConverter operatorTOConverter) {
        super(jpqlWhereBuilder, jpqlOrderByBuilder, batchLoadSize);
        this.operatorTOConverter = operatorTOConverter;
    }

    @Override
    public String getTypeName() {
        return "Train";
    }

    @Override
    public String getFieldName() {
        return "operator";
    }

    @Override
    public String createKeyFromParent(final TrainTO trainTO) {
        return trainTO.getOperatorShortCode();
    }

    @Override
    public String createKeyFromChild(final OperatorTO operatorTO) {
        return operatorTO.getShortCode();
    }

    @Override
    public OperatorTO createChildTOFromEntity(final Operator entity) {
        return operatorTOConverter.convertEntity(entity);
    }

    @Override
    public Class<Operator> getEntityClass() {
        return Operator.class;
    }

    @Override
    public String createWhereClause(final List<String> keys) {
        return getEntityAlias() + ".shortCode IN :keys";
    }

    @Override
    public String getDefaultOrderBy() {
        return getEntityAlias() + ".shortCode ASC";
    }
}

