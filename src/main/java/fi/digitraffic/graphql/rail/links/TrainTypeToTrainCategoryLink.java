package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import fi.digitraffic.graphql.rail.entities.QTrainCategory;
import fi.digitraffic.graphql.rail.entities.QTrainType;
import fi.digitraffic.graphql.rail.entities.TrainCategory;
import fi.digitraffic.graphql.rail.links.base.OneToOneLink;
import fi.digitraffic.graphql.rail.model.TrainCategoryTO;
import fi.digitraffic.graphql.rail.model.TrainTypeTO;
import fi.digitraffic.graphql.rail.to.TrainCategoryTOConverter;

@Component
public class TrainTypeToTrainCategoryLink extends OneToOneLink<Long, TrainTypeTO, TrainCategory, TrainCategoryTO> {
    @Autowired
    private TrainCategoryTOConverter trainCategoryTOConverter;

    @Override
    public String getTypeName() {
        return "TrainType";
    }

    @Override
    public String getFieldName() {
        return "trainCategory";
    }

    @Override
    public Long createKeyFromParent(final TrainTypeTO trainTypeTO) {
        return Long.valueOf(trainTypeTO.getTrainCategoryId());
    }

    @Override
    public Long createKeyFromChild(final TrainCategoryTO trainCategoryTO) {
        return Long.valueOf(trainCategoryTO.getId());
    }

    @Override
    public TrainCategoryTO createChildTOFromTuple(final Tuple tuple) {
        return trainCategoryTOConverter.convert(tuple);
    }

    @Override
    public Class getEntityClass() {
        return TrainCategory.class;
    }

    @Override
    public EntityPath getEntityTable() {
        return QTrainCategory.trainCategory;
    }

    @Override
    public BooleanExpression createWhere(final List<Long> keys) {
        return QTrainCategory.trainCategory.id.in(keys);
    }

    @Override
    public List<Expression<?>> columnsNeededFromParentTable() {
        return List.of(QTrainType.trainType.trainCategoryId);
    }
}
