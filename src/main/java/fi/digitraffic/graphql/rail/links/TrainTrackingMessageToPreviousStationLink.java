package fi.digitraffic.graphql.rail.links;

import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import fi.digitraffic.graphql.rail.model.TrainTrackingMessageTO;

@Component
public class TrainTrackingMessageToPreviousStationLink extends TrainTrackingMessageToNextStationLink {
    @Override
    public String getFieldName() {
        return "previousStation";
    }

    @Override
    public String createKeyFromParent(TrainTrackingMessageTO trainTrackingMessageTO) {
        return Strings.nullToEmpty(trainTrackingMessageTO.getPreviousStationShortCode());
    }
}
