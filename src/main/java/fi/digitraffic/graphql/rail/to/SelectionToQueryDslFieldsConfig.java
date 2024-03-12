package fi.digitraffic.graphql.rail.to;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.google.common.collect.Streams;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import fi.digitraffic.graphql.rail.entities.QCategoryCode;
import fi.digitraffic.graphql.rail.entities.QCause;
import fi.digitraffic.graphql.rail.entities.QComposition;
import fi.digitraffic.graphql.rail.entities.QDetailedCategoryCode;
import fi.digitraffic.graphql.rail.entities.QJourneySection;
import fi.digitraffic.graphql.rail.entities.QLocomotive;
import fi.digitraffic.graphql.rail.entities.QOperator;
import fi.digitraffic.graphql.rail.entities.QRoutesection;
import fi.digitraffic.graphql.rail.entities.QRoutesetMessage;
import fi.digitraffic.graphql.rail.entities.QStation;
import fi.digitraffic.graphql.rail.entities.QThirdCategoryCode;
import fi.digitraffic.graphql.rail.entities.QTimeTableRow;
import fi.digitraffic.graphql.rail.entities.QTrackRange;
import fi.digitraffic.graphql.rail.entities.QTrackSection;
import fi.digitraffic.graphql.rail.entities.QTrain;
import fi.digitraffic.graphql.rail.entities.QTrainCategory;
import fi.digitraffic.graphql.rail.entities.QTrainLocation;
import fi.digitraffic.graphql.rail.entities.QTrainTrackingMessage;
import fi.digitraffic.graphql.rail.entities.QTrainType;
import fi.digitraffic.graphql.rail.entities.QWagon;
import fi.digitraffic.graphql.rail.links.base.BaseLink;
import fi.digitraffic.graphql.rail.model.CategoryCodeTO;
import fi.digitraffic.graphql.rail.model.CauseTO;
import fi.digitraffic.graphql.rail.model.CompositionTO;
import fi.digitraffic.graphql.rail.model.DetailedCategoryCodeTO;
import fi.digitraffic.graphql.rail.model.JourneySectionTO;
import fi.digitraffic.graphql.rail.model.LocomotiveTO;
import fi.digitraffic.graphql.rail.model.OperatorTO;
import fi.digitraffic.graphql.rail.model.RoutesectionTO;
import fi.digitraffic.graphql.rail.model.RoutesetMessageTO;
import fi.digitraffic.graphql.rail.model.StationTO;
import fi.digitraffic.graphql.rail.model.ThirdCategoryCodeTO;
import fi.digitraffic.graphql.rail.model.TimeTableRowTO;
import fi.digitraffic.graphql.rail.model.TrackRangeTO;
import fi.digitraffic.graphql.rail.model.TrackSectionTO;
import fi.digitraffic.graphql.rail.model.TrainCategoryTO;
import fi.digitraffic.graphql.rail.model.TrainLocationTO;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.model.TrainTrackingMessageTO;
import fi.digitraffic.graphql.rail.model.TrainTypeTO;
import fi.digitraffic.graphql.rail.model.WagonTO;
import jakarta.annotation.PostConstruct;

@Service
public class SelectionToQueryDslFieldsConfig {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private static Map<EntityPath<?>, Set<Expression>> ID_FIELDS = new HashMap<>();
    private static Map<Field, Set<Expression>> EXCEPTION_FIELDS = new HashMap<>();

    private Map<EntityPath<?>, Map<String, Set<Expression>>> fieldConversions = new HashMap();

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void setup() throws NoSuchFieldException {
        // Bind QueryDsl fields to GraphQL fields
        this.registerPair(TrainTO.class, QTrain.train);
        this.registerPair(OperatorTO.class, QOperator.operator);
        this.registerPair(CompositionTO.class, QComposition.composition);
        this.registerPair(JourneySectionTO.class, QJourneySection.journeySection);
        this.registerPair(TimeTableRowTO.class, QTimeTableRow.timeTableRow);
        this.registerPair(CategoryCodeTO.class, QCategoryCode.categoryCode);
        this.registerPair(CauseTO.class, QCause.cause);
        this.registerPair(DetailedCategoryCodeTO.class, QDetailedCategoryCode.detailedCategoryCode);
        this.registerPair(LocomotiveTO.class, QLocomotive.locomotive);
        this.registerPair(RoutesectionTO.class, QRoutesection.routesection);
        this.registerPair(RoutesetMessageTO.class, QRoutesetMessage.routesetMessage);
        this.registerPair(StationTO.class, QStation.station);
        this.registerPair(ThirdCategoryCodeTO.class, QThirdCategoryCode.thirdCategoryCode);
        this.registerPair(TrackRangeTO.class, QTrackRange.trackRange);
        this.registerPair(TrackSectionTO.class, QTrackSection.trackSection);
        this.registerPair(TrainCategoryTO.class, QTrainCategory.trainCategory);
        this.registerPair(TrainLocationTO.class, QTrainLocation.trainLocation);
        this.registerPair(TrainTrackingMessageTO.class, QTrainTrackingMessage.trainTrackingMessage);
        this.registerPair(TrainTypeTO.class, QTrainType.trainType);
        this.registerPair(WagonTO.class, QWagon.wagon);

        // Id Fields
        ID_FIELDS.put(QTrain.train, Set.of(QTrain.train.id));
        ID_FIELDS.put(QOperator.operator, Set.of(QOperator.operator.shortCode));
        ID_FIELDS.put(QComposition.composition, Set.of(QComposition.composition.id));
        ID_FIELDS.put(QJourneySection.journeySection, Set.of(QJourneySection.journeySection.id, QJourneySection.journeySection.trainId));
        ID_FIELDS.put(QTimeTableRow.timeTableRow, Set.of(QTimeTableRow.timeTableRow.id));
        ID_FIELDS.put(QCategoryCode.categoryCode, Set.of(QCategoryCode.categoryCode.oid));
        ID_FIELDS.put(QCause.cause, Set.of(QCause.cause.timeTableRowId,QCause.cause.id));
        ID_FIELDS.put(QDetailedCategoryCode.detailedCategoryCode, Set.of(QDetailedCategoryCode.detailedCategoryCode.oid));
        ID_FIELDS.put(QLocomotive.locomotive, Set.of(QLocomotive.locomotive.id, QLocomotive.locomotive.journeysectionId));
        ID_FIELDS.put(QRoutesection.routesection, Set.of(QRoutesection.routesection.routesetId));
        ID_FIELDS.put(QRoutesetMessage.routesetMessage, Set.of(QRoutesetMessage.routesetMessage.id, QRoutesetMessage.routesetMessage.trainId));
        ID_FIELDS.put(QStation.station, Set.of(QStation.station.id, QStation.station.shortCode));
        ID_FIELDS.put(QThirdCategoryCode.thirdCategoryCode, Set.of(QThirdCategoryCode.thirdCategoryCode.oid));
        ID_FIELDS.put(QTrackRange.trackRange, Set.of(QTrackRange.trackRange.id, QTrackRange.trackRange.trackSectionId));
        ID_FIELDS.put(QTrackSection.trackSection, Set.of(QTrackSection.trackSection.id, QTrackSection.trackSection.trackSectionCode));
        ID_FIELDS.put(QTrainCategory.trainCategory, Set.of(QTrainCategory.trainCategory.id));
        ID_FIELDS.put(QTrainLocation.trainLocation, Set.of(QTrainLocation.trainLocation.id, QTrainLocation.trainLocation.trainLocationId));
        ID_FIELDS.put(QTrainTrackingMessage.trainTrackingMessage, Set.of(QTrainTrackingMessage.trainTrackingMessage.id, QTrainTrackingMessage.trainTrackingMessage.trainId));
        ID_FIELDS.put(QTrainType.trainType, Set.of(QTrainType.trainType.id));
        ID_FIELDS.put(QWagon.wagon, Set.of(QWagon.wagon.id, QWagon.wagon.journeysectionId));

        // Differing field names
        EXCEPTION_FIELDS.put(TimeTableRowTO.class.getDeclaredField("estimateSourceType"), Set.of(QTimeTableRow.timeTableRow.estimateSource));
        EXCEPTION_FIELDS.put(OperatorTO.class.getDeclaredField("uicCode"), Set.of(QOperator.operator.operatorUicCode));
        EXCEPTION_FIELDS.put(CauseTO.class.getDeclaredField("trainNumber"), Set.of(QCause.cause.timeTableRowId.trainNumber));
        EXCEPTION_FIELDS.put(CauseTO.class.getDeclaredField("departureDate"), Set.of(QCause.cause.timeTableRowId.departureDate));
        EXCEPTION_FIELDS.put(CauseTO.class.getDeclaredField("timeTableRowId"), Set.of(QCause.cause.timeTableRowId.attapId));
        EXCEPTION_FIELDS.put(JourneySectionTO.class.getDeclaredField("trainNumber"), Set.of(QJourneySection.journeySection.trainId.trainNumber));
        EXCEPTION_FIELDS.put(JourneySectionTO.class.getDeclaredField("departureDate"), Set.of(QJourneySection.journeySection.trainId.departureDate));
        EXCEPTION_FIELDS.put(JourneySectionTO.class.getDeclaredField("beginTimeTableRowId"), Set.of(QJourneySection.journeySection.attapId));
        EXCEPTION_FIELDS.put(JourneySectionTO.class.getDeclaredField("endTimeTableRowId"), Set.of(QJourneySection.journeySection.saapAttapId));
        EXCEPTION_FIELDS.put(RoutesetMessageTO.class.getDeclaredField("trainNumber"), Set.of(QRoutesetMessage.routesetMessage.trainId.trainNumber));
        EXCEPTION_FIELDS.put(StationTO.class.getDeclaredField("location"), Set.of(QStation.station.latitude, QStation.station.longitude));
    }

    public <T> Expression[] getDSLFields(final EntityPath<T> entity, final List<graphql.language.Field> selections) {
        var idFields = ID_FIELDS.get(entity);

        var entitysFetchers = getEntitysFetchers(entity);

        var dslFieldMap = this.fieldConversions.get(entity);

        Set<Expression<T>> linkFields = new HashSet<>();
        Set<Expression<T>> selectionFields = new HashSet<>();
        for (final graphql.language.Field selection : selections) {
            var link = entitysFetchers.get(selection.getName());
            if (link != null) {
                linkFields.addAll(link.columnsNeededFromParentTable());
            } else {
                for (final Expression expression : dslFieldMap.getOrDefault(selection.getName(), Set.of())) {
                    selectionFields.add(expression);
                }
            }
        }

        return Streams.concat(idFields.stream(), linkFields.stream(),selectionFields.stream()).collect(Collectors.toSet()).stream().toArray(size -> new Expression[size]);
    }

    private <T> void registerPair(Class<?> graphQLEntity, EntityPath<T> queryDslEntity) {
        try {
            var queryDslEntityClass = queryDslEntity.getClass();
            var fieldsMap = this.fieldConversions.get(queryDslEntity);
            if (fieldsMap == null) {
                fieldsMap = new HashMap<>();
                this.fieldConversions.put(queryDslEntity, fieldsMap);
            }

            for (final Field graphQLField : graphQLEntity.getDeclaredFields()) {
                if (Modifier.isStatic(graphQLField.getModifiers())) {
                    continue;
                }


                final Set<Expression> expression = this.EXCEPTION_FIELDS.get(graphQLField);
                var gqlFieldName = graphQLField.getName();
                if (expression != null) {
                    fieldsMap.put(gqlFieldName, expression);
                } else {
                    try {
                        final Field declaredField = queryDslEntityClass.getDeclaredField(gqlFieldName);
                        var qdslExpression = (Expression<T>) declaredField.get(queryDslEntity);
                        fieldsMap.put(gqlFieldName, Set.of(qdslExpression));
                    }
                    catch (NoSuchFieldException noSuchFieldException) {
                        log.info(String.format("No field found for: %s: %s", queryDslEntityClass.getSimpleName(), gqlFieldName));
                    }
                }
            }
        } catch (ReflectiveOperationException e) {
            log.error(String.format("Error initializing %s", queryDslEntity),e);
        }
    }

    private <T> Map<String, BaseLink> getEntitysFetchers(final EntityPath<T> entity) {
        final var allFetchers = Arrays.stream(applicationContext.getBeanNamesForType(BaseLink.class))
            .map(s -> (BaseLink) applicationContext.getBean(s))
            .filter(s -> s.getTypeName().equals(entity.getType().getSimpleName()))
            .collect(Collectors.toList());

        var entitysFetchers = allFetchers.stream()
            .collect(Collectors.toMap(s -> s.getFieldName(), s -> s));
        return entitysFetchers;
    }
}
