package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import fi.digitraffic.graphql.rail.entities.QTrainCategory;
import fi.digitraffic.graphql.rail.model.TrainCategoryTO;

@Component
public class TrainCategoryTOConverter extends BaseConverter<TrainCategoryTO> {
    @Override
    public TrainCategoryTO convert(final Tuple tuple) {
        return new TrainCategoryTO(
                tuple.get(QTrainCategory.trainCategory.id).longValue(),
                tuple.get(QTrainCategory.trainCategory.name)
        );
    }
}
