package fi.digitraffic.graphql.rail.links;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.links.base.KeyWhereClause;
import fi.digitraffic.graphql.rail.links.base.OneToOneLink;
import fi.digitraffic.graphql.rail.links.base.TrainIdWhereClause;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageTO;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.query.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.query.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.TrainTOConverter;

@Component
public class PassengerInformationMessageToTrainLink extends OneToOneLink<TrainId, PassengerInformationMessageTO, Train, TrainTO> {

    private final TrainTOConverter trainTOConverter;

    public PassengerInformationMessageToTrainLink(final JpqlWhereBuilder jpqlWhereBuilder,
                                                  final JpqlOrderByBuilder jpqlOrderByBuilder,
                                                  @Value("${digitraffic.batch-load-size:500}") final int batchLoadSize,
                                                  final TrainTOConverter trainTOConverter) {
        super(jpqlWhereBuilder, jpqlOrderByBuilder, batchLoadSize);
        this.trainTOConverter = trainTOConverter;
    }

    @Override
    public String getTypeName() { return "PassengerInformationMessage"; }

    @Override
    public String getFieldName() { return "train"; }

    @Override
    public TrainId createKeyFromParent(final PassengerInformationMessageTO msg) {
        return (msg.getTrainNumber() != null && msg.getTrainDepartureDate() != null)
                ? new TrainId(msg.getTrainNumber(), msg.getTrainDepartureDate())
                : null;
    }

    @Override
    public TrainId createKeyFromChild(final TrainTO trainTO) {
        return new TrainId(trainTO.getTrainNumber(), trainTO.getDepartureDate());
    }

    @Override
    public TrainTO createChildTOFromEntity(final Train entity) {
        return trainTOConverter.convertEntity(entity);
    }

    @Override
    public Class<Train> getEntityClass() { return Train.class; }

    @Override
    protected KeyWhereClause buildKeyWhereClause(final List<TrainId> keys) {
        final List<TrainId> nonNullKeys = keys.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (nonNullKeys.isEmpty()) {
            return new KeyWhereClause("1 = 0", java.util.Map.of());
        }
        return TrainIdWhereClause.build(getEntityAlias(), "id.departureDate", "id.trainNumber", nonNullKeys);
    }
}

