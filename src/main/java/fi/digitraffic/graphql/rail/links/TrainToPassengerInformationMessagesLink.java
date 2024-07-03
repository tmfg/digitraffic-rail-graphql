package fi.digitraffic.graphql.rail.links;

import static fi.digitraffic.graphql.rail.queries.PassengerInformationMessagesQuery.getPassengerInformationBaseQuery;

import java.util.List;

import org.dataloader.BatchLoaderWithContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessage;
import fi.digitraffic.graphql.rail.entities.QPassengerInformationMessage;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.links.base.OneToManyLink;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageTO;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.querydsl.AllFields;
import fi.digitraffic.graphql.rail.repositories.TrainIdOptimizer;
import fi.digitraffic.graphql.rail.to.PassengerInformationMessageTOConverter;

@Component
public class TrainToPassengerInformationMessagesLink extends
        OneToManyLink<TrainId, TrainTO, PassengerInformationMessage, PassengerInformationMessageTO> {
    @Autowired
    private PassengerInformationMessageTOConverter passengerInformationMessageTOConverter;

    @Override
    public String getTypeName() {
        return "Train";
    }

    @Override
    public String getFieldName() {
        return "passengerInformationMessages";
    }

    @Override
    public TrainId createKeyFromParent(final TrainTO trainTO) {
        return new TrainId(trainTO.getTrainNumber(), trainTO.getDepartureDate());
    }

    @Override
    public TrainId createKeyFromChild(final PassengerInformationMessageTO child) {
        if (child == null) {
            return null;
        }
        return new TrainId(child.getTrainNumber(), child.getTrainDepartureDate());
    }

    @Override
    public PassengerInformationMessageTO createChildTOFromTuple(final Tuple tuple) {
        return passengerInformationMessageTOConverter.convert(tuple);
    }

    @Override
    public Class<PassengerInformationMessage> getEntityClass() {
        return PassengerInformationMessage.class;
    }

    @Override
    public Expression[] getFields() {
        return AllFields.PASSENGER_INFORMATION_MESSAGE;
    }

    @Override
    public EntityPath getEntityTable() {
        return QPassengerInformationMessage.passengerInformationMessage;
    }

    @Override
    public BatchLoaderWithContext<TrainId, List<PassengerInformationMessageTO>> createLoader() {
        final JPAQuery<Tuple> queryAfterFrom = getPassengerInformationBaseQuery(super.queryFactory, getEntityTable());
        return doCreateLoader(queryAfterFrom);
    }

    @Override
    public BooleanExpression createWhere(final List<TrainId> keys) {
        return TrainIdOptimizer.optimize(QPassengerInformationMessage.passengerInformationMessage.train.id, keys);
    }

}

