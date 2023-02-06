package org.iatoki.judgels.play;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;

public final class JudgelsPlayUtils {

    private JudgelsPlayUtils() {}

    public static String formatDetailedDateTime(long timestamp) {
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss XXX");
        return formatter.format(zonedDateTime);
    }

    public static String formatISOUTCDateTime(long timestamp) {
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        return formatter.format(zonedDateTime);
    }

    public static String formatBytesCount(long bytes) {
        return FileUtils.byteCountToDisplaySize(bytes);
    }

    public static String escapeHtmlString(String string) {
        return StringEscapeUtils.escapeHtml4(string).replaceAll("\r\n", "<br />").replaceAll("\n", "<br />");
    }

    public static String toSafeHtml(String html) {
        return html;
    }
}
