package org.iatoki.judgels.play;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.auth.UsernamePasswordCredentials;
import play.mvc.Http;

public final class JudgelsPlayUtils {

    private JudgelsPlayUtils() {
        // prevents instantiation
    }

    public static String formatDate(long timestamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return simpleDateFormat.format(new Date(timestamp));
    }

    public static long parseDate(String string) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            return simpleDateFormat.parse(string).getTime();
        } catch (ParseException e) {
            return 0;
        }
    }

    public static String formatDateTime(long timestamp) {
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss XXX");
        return formatter.format(zonedDateTime);
    }

    public static long parseDateTime(String string) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss XXX");
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(string, formatter);
        return zonedDateTime.toInstant().toEpochMilli();
    }

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

    public static String generateNewSecret() {
        return hashMD5(UUID.randomUUID().toString());
    }

    public static String hashSHA256(String s) {
        return messageDigest(s, MessageDigestAlgorithms.SHA_256);
    }

    public static String hashMD5(String s) {
        return messageDigest(s, MessageDigestAlgorithms.MD5);
    }

    public static String escapeHtmlString(String string) {
        return StringEscapeUtils.escapeHtml4(string).replaceAll("\r\n", "<br />").replaceAll("\n", "<br />");
    }

    public static boolean isSidebarShown(Http.Request request) {
        return (request.cookie("sidebar") == null) || (request.cookie("sidebar").value().equals("true"));
    }

    public static boolean isFullscreen(Http.Request request) {
        return (request.cookie("fullscreen") != null) && (request.cookie("fullscreen").value().equals("true"));
    }

    public static UsernamePasswordCredentials parseBasicAuthFromRequest(Http.Request request) {
        UsernamePasswordCredentials credentials = null;
        if (request.getHeaders().get("Authorization").orElse("").startsWith("Basic ")) {
            credentials = new UsernamePasswordCredentials(new String(Base64.decodeBase64(request.getHeaders().get("Authorization").get().substring("Basic ".length()))));
        }

        return credentials;
    }

    public static String toSafeHtml(String html) {
        return html;
    }

    private static String messageDigest(String s, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] hash = md.digest(s.getBytes("UTF-8"));
            return new String(Hex.encodeHex(hash));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private static void putInSession(String key, String value) {
        Http.Context.current().session().put(key, value);
    }

    private static String getFromSession(String key) {
        return Http.Context.current().session().get(key);
    }
}
