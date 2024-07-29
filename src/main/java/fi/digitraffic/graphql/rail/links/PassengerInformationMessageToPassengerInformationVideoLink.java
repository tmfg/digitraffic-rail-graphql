package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessageId;
import fi.digitraffic.graphql.rail.entities.PassengerInformationVideo;
import fi.digitraffic.graphql.rail.entities.QPassengerInformationVideo;
import fi.digitraffic.graphql.rail.links.base.OneToOneLink;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationVideoTO;
import fi.digitraffic.graphql.rail.querydsl.AllFields;
import fi.digitraffic.graphql.rail.to.PassengerInformationVideoTOConverter;

@Component
public class PassengerInformationMessageToPassengerInformationVideoLink extends
        OneToOneLink<PassengerInformationMessageId, PassengerInformationMessageTO, PassengerInformationVideo, PassengerInformationVideoTO> {

    @Autowired
    private PassengerInformationVideoTOConverter videoTOConverter;

    @Override
    public String getTypeName() {
        return "PassengerInformationMessage";
    }

    @Override
    public String getFieldName() {
        return "video";
    }

    @Override
    public PassengerInformationMessageId createKeyFromParent(final PassengerInformationMessageTO passengerInformationMessageTO) {
        return new PassengerInformationMessageId(passengerInformationMessageTO.getId(), passengerInformationMessageTO.getVersion());
    }

    @Override
    public PassengerInformationMessageId createKeyFromChild(final PassengerInformationVideoTO videoTO) {
        if (videoTO == null) {
            return null;
        }
        return new PassengerInformationMessageId(videoTO.getMessageId(), videoTO.getMessageVersion());
    }

    @Override
    public PassengerInformationVideoTO createChildTOFromTuple(final Tuple tuple) {
        return videoTOConverter.convert(tuple);
    }

    @Override
    public Class getEntityClass() {
        return PassengerInformationVideo.class;
    }

    @Override
    public Expression[] getFields() {
        return AllFields.PASSENGER_INFORMATION_MESSAGE_VIDEO;
    }

    @Override
    public EntityPath getEntityTable() {
        return QPassengerInformationVideo.passengerInformationVideo;
    }

    @Override
    public BooleanExpression createWhere(final List<PassengerInformationMessageId> keys) {
        return QPassengerInformationVideo.passengerInformationVideo.message.id.in(keys);
    }

}
