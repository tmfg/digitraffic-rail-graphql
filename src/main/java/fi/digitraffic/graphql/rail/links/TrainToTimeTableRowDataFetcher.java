package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.TimeTableRow;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.links.base.OneToManyDataFetcher;
import fi.digitraffic.graphql.rail.model.TimeTableRowTO;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.repositories.TimeTableRowRepository;
import fi.digitraffic.graphql.rail.to.TimeTableRowTOConverter;

@Component
public class TrainToTimeTableRowDataFetcher extends OneToManyDataFetcher<TrainId, TrainTO, TimeTableRow, TimeTableRowTO> {
    @Autowired
    private TimeTableRowRepository timeTableRowRepository;

    @Autowired
    private TimeTableRowTOConverter timeTableRowTOConverter;

    @Override
    public String getTypeName() {
        return "Train";
    }

    @Override
    public String getFieldName() {
        return "timeTableRows";
    }

    @Override
    public TrainId createKeyFromParent(TrainTO trainTO) {
        return new TrainId(trainTO.getTrainNumber().longValue(), trainTO.getDepartureDate());
    }

    @Override
    public TrainId createKeyFromChild(TimeTableRow child) {
        return new TrainId(child.id.trainNumber, child.id.departureDate);
    }

    @Override
    public TimeTableRowTO createChildTOToFromChild(TimeTableRow child) {
        return timeTableRowTOConverter.convert(child);
    }

    @Override
    public List<TimeTableRow> findChildrenByKeys(List<TrainId> keys) {
        return timeTableRowRepository.findAllByTrainIds(keys);
    }
}
