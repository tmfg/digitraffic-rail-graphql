package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import fi.digitraffic.graphql.rail.entities.QWagon;
import fi.digitraffic.graphql.rail.entities.Wagon;
import fi.digitraffic.graphql.rail.links.base.OneToManyLink;
import fi.digitraffic.graphql.rail.model.JourneySectionTO;
import fi.digitraffic.graphql.rail.model.WagonTO;
import fi.digitraffic.graphql.rail.querydsl.AllFields;
import fi.digitraffic.graphql.rail.to.WagonTOConverter;

@Component
public class JourneySectionToWagonLink extends OneToManyLink<Long, JourneySectionTO, Wagon, WagonTO> {
    @Autowired
    private WagonTOConverter wagonTOConverter;

    @Override
    public String getTypeName() {
        return "JourneySection";
    }

    @Override
    public String getFieldName() {
        return "wagons";
    }

    @Override
    public Long createKeyFromParent(final JourneySectionTO journeySectionTO) {
        return Long.valueOf(journeySectionTO.getId());
    }

    @Override
    public Long createKeyFromChild(final WagonTO wagonTO) {
        return Long.valueOf(wagonTO.getJourneysectionId());
    }

    @Override
    public WagonTO createChildTOFromTuple(final Tuple tuple) {
        return wagonTOConverter.convert(tuple);
    }

    @Override
    public Class getEntityClass() {
        return Wagon.class;
    }

    @Override
    public Expression[] getFields() {
        return AllFields.WAGON;
    }

    @Override
    public EntityPath getEntityTable() {
        return QWagon.wagon;
    }

    @Override
    public BooleanExpression createWhere(final List<Long> keys) {
        return QWagon.wagon.journeysectionId.in(keys);
    }
}
