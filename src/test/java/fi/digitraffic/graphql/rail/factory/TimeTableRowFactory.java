package fi.digitraffic.graphql.rail.factory;


import java.time.LocalDate;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Station;
import fi.digitraffic.graphql.rail.entities.TimeTableRow;
import fi.digitraffic.graphql.rail.entities.TimeTableRowId;
import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.repositories.TimeTableRowRepository;

@Component
public class TimeTableRowFactory {
    public long attapId = 1L;

    @Autowired
    private TimeTableRowRepository timeTableRowRepository;

    public TimeTableRow create(final Train train, final ZonedDateTime scheduledTime, final ZonedDateTime actualTime,
                               final Station station, final TimeTableRow.TimeTableRowType timeTableRowType) {
        final String stationShortCode = station.shortCode;
        final int stationcUICCode = station.uicCode;
        final String countryCode = station.countryCode;
        final TimeTableRow.TimeTableRowType type = timeTableRowType;
        final String commercialTrack = "1";
        final boolean cancelled = false;
        final ZonedDateTime liveEstimateTime = null;

        final Long differenceInMinutes = null;
        final long atappiId = attapId++;

        final long trainNumber = train.id.trainNumber;
        final LocalDate departureDate = train.id.departureDate;
        final boolean commercialStop = true;
        final long version = 1L;
//        final TimeTableRow timeTableRow = new TimeTableRow(stationShortCode, stationcUICCode, countryCode, type, commercialTrack, cancelled,
//                scheduledTime, liveEstimateTime, actualTime, differenceInMinutes, atappiId, trainNumber, departureDate, commercialStop,
//                version,null, TimeTableRow.EstimateSourceEnum.LIIKE_AUTOMATIC);
        final TimeTableRow timeTableRow = new TimeTableRow();
        timeTableRow.actualTime = actualTime;
        timeTableRow.cancelled = cancelled;
        timeTableRow.commercialStop = commercialStop;
        timeTableRow.commercialTrack = commercialTrack;
        timeTableRow.countryCode = countryCode;
        timeTableRow.differenceInMinutes = differenceInMinutes;
        timeTableRow.estimateSource = TimeTableRow.EstimateSourceEnum.LIIKE_AUTOMATIC;
        timeTableRow.liveEstimateTime = liveEstimateTime;
        timeTableRow.scheduledTime = scheduledTime;
        timeTableRow.stationShortCode = stationShortCode;
        timeTableRow.stationUICCode = stationcUICCode;
        timeTableRow.train = train;
        timeTableRow.trainStopping = true;
        timeTableRow.type = type;
        timeTableRow.unknownDelay = null;
        timeTableRow.id = new TimeTableRowId(atappiId, departureDate, trainNumber);

        return timeTableRowRepository.save(timeTableRow);
    }
}
