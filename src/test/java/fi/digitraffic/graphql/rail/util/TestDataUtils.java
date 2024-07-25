package fi.digitraffic.graphql.rail.util;

import java.time.format.DateTimeFormatter;

public class TestDataUtils {

    public static final String INSERT_INTO_RAMI_MESSAGE_SQL =
            "INSERT INTO rami_message (id, version, created_source, start_validity, end_validity, train_number, train_departure_date, message_type) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String INSERT_INTO_RAMI_MESSAGE_STATION_SQL =
            "INSERT INTO rami_message_station (id, rami_message_id, rami_message_version, station_short_code) " +
                    "VALUES (?, ?, ?, ?)";

    public static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final String HKI = "HKI";
    public static final String TPE = "TPE";
}