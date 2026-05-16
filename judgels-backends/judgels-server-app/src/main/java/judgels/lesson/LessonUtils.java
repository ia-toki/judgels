package judgels.lesson;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.UriInfo;

public class LessonUtils {
    private LessonUtils() {}

    public static String replaceLessonRenderUrls(String text, String apiUrl, String lessonJid) {
        return text
                .replaceAll(
                        "src=\"render/",
                        String.format("src=\"%sapi/v2/lessons/%s/render/", apiUrl, lessonJid))
                .replaceAll(
                        "url=render/",
                        String.format("url=%sapi/v2/lessons/%s/render/", apiUrl, lessonJid))
                .replaceAll(
                        "href=\"render/",
                        String.format("href=\"%sapi/v2/lessons/%s/render/", apiUrl, lessonJid));
    }

    public static String getApiUrl(HttpServletRequest req, UriInfo uriInfo) {
        if (req == null) {
            return "";
        }

        String oldScheme = uriInfo.getBaseUri().getScheme();
        String newScheme = oldScheme;

        String forwardedProto = req.getHeader("X-Forwarded-Proto");
        if (forwardedProto != null && !forwardedProto.isEmpty()) {
            newScheme = forwardedProto;
        }

        return newScheme + uriInfo.getBaseUri().toString().substring(oldScheme.length());
    }
}
