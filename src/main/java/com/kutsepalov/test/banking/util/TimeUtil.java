package com.kutsepalov.test.banking.util;

import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class TimeUtil {

    public static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");

    public static Timestamp now() {
        return Timestamp.from(ZonedDateTime.now(UTC_ZONE_ID).toInstant());
    }

    public static ZonedDateTime nowUTC() {
        return ZonedDateTime.now(UTC_ZONE_ID);
    }

    public static ZonedDateTime toZonedDateTime(Timestamp timestamp) {
        return timestamp.toInstant().atZone(UTC_ZONE_ID);
    }

    public static Timestamp toTimestamp(ZonedDateTime zonedDateTime) {
        return Timestamp.from(zonedDateTime.toInstant());
    }
}
