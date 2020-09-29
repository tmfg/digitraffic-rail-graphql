package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import fi.digitraffic.graphql.rail.entities.QTrainType;
import fi.digitraffic.graphql.rail.model.TrainTypeTO;

@Component
public class TrainTypeTOConverter extends BaseConverter<TrainTypeTO> {
    @Override
    public TrainTypeTO convert(Tuple tuple) {
        return new TrainTypeTO(
                tuple.get(QTrainType.trainType.id).intValue(),
                tuple.get(QTrainType.trainType.name),
                tuple.get(QTrainType.trainType.trainCategoryId).intValue()
                , null
        );
    }
}
