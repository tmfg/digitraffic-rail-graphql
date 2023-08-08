package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import fi.digitraffic.graphql.rail.entities.Locomotive;
import fi.digitraffic.graphql.rail.entities.QLocomotive;
import fi.digitraffic.graphql.rail.links.base.OneToManyLink;
import fi.digitraffic.graphql.rail.model.JourneySectionTO;
import fi.digitraffic.graphql.rail.model.LocomotiveTO;
import fi.digitraffic.graphql.rail.querydsl.AllFields;
import fi.digitraffic.graphql.rail.to.LocomotiveTOConverter;

@Component
public class JourneySectionToLocomotiveLink extends OneToManyLink<Long, JourneySectionTO, Locomotive, LocomotiveTO> {
    @Autowired
    private LocomotiveTOConverter locomotiveTOConverter;

    @Override
    public String getTypeName() {
        return "JourneySection";
    }

    @Override
    public String getFieldName() {
        return "locomotives";
    }

    @Override
    public Long createKeyFromParent(final JourneySectionTO journeySectionTO) {
        return journeySectionTO.getId();
    }

    @Override
    public Long createKeyFromChild(final LocomotiveTO locomotiveTO) {
        return locomotiveTO.getJourneysectionId();
    }

    @Override
    public LocomotiveTO createChildTOFromTuple(final Tuple tuple) {
        return locomotiveTOConverter.convert(tuple);
    }

    @Override
    public Class getEntityClass() {
        return Locomotive.class;
    }

    @Override
    public Expression[] getFields() {
        return AllFields.LOCOMOTIVE;
    }

    @Override
    public EntityPath getEntityTable() {
        return QLocomotive.locomotive;
    }

    @Override
    public BooleanExpression createWhere(final List<Long> keys) {
        return QLocomotive.locomotive.journeysectionId.in(keys);
    }
}
