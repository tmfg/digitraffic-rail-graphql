package fi.digitraffic.graphql.rail.util;

import java.time.format.DateTimeFormatter;

import org.springframework.jdbc.core.JdbcTemplate;

public class TestDataUtils {

    public static int insertRamiMessage(final JdbcTemplate jdbcTemplate, final String id, final int version, final String createdDateTime,
                                        final String startValidity, final String endValidity, final Integer trainNumber,
                                        final String trainDepartureDate,
                                        final String messageType) {
        return jdbcTemplate.update(INSERT_INTO_RAMI_MESSAGE_SQL, id, version, createdDateTime, startValidity, endValidity, trainNumber,
                trainDepartureDate,
                messageType);
    }

    public static int insertRamiMessageStation(final JdbcTemplate jdbcTemplate, final String ramiMessageId,
                                               final int ramiMessageVersion, final String stationShortCode) {
        return jdbcTemplate.update(INSERT_INTO_RAMI_MESSAGE_STATION_SQL, ramiMessageId, ramiMessageVersion, stationShortCode);
    }

    public static final String INSERT_INTO_RAMI_MESSAGE_SQL =
            "INSERT INTO rami_message (id, version, created_source, start_validity, end_validity, train_number, train_departure_date, message_type) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String INSERT_INTO_RAMI_MESSAGE_STATION_SQL =
            "INSERT INTO rami_message_station (rami_message_id, rami_message_version, station_short_code) " +
                    "VALUES (?, ?, ?)";

    public static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final String HKI = "HKI";
    public static final String TPE = "TPE";
}