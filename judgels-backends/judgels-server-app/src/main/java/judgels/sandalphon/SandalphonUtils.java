package judgels.sandalphon;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.UriInfo;
import java.util.Optional;
import judgels.api.lesson.LessonInfo;
import judgels.api.problem.ProblemInfo;

public class SandalphonUtils {
    private SandalphonUtils() {}

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

    public static String getProblemName(ProblemInfo problem, Optional<String> language) {
        String finalLanguage = problem.getDefaultLanguage();
        if (language.isPresent() && problem.getTitlesByLanguage().containsKey(language.get())) {
            finalLanguage = language.get();
        }
        return problem.getTitlesByLanguage().get(finalLanguage);
    }

    public static String getLessonName(LessonInfo lesson, Optional<String> language) {
        String finalLanguage = lesson.getDefaultLanguage();
        if (language.isPresent() && lesson.getTitlesByLanguage().containsKey(language.get())) {
            finalLanguage = language.get();
        }
        return lesson.getTitlesByLanguage().get(finalLanguage);
    }

    public static String replaceProblemRenderUrls(String text, String apiUrl, String problemJid) {
        return text
                .replaceAll(
                        "src=\"render/",
                        String.format("src=\"%sapi/v2/problems/%s/render/", apiUrl, problemJid))
                .replaceAll(
                        "url=render/",
                        String.format("url=%sapi/v2/problems/%s/render/", apiUrl, problemJid))
                .replaceAll(
                        "href=\"render/",
                        String.format("href=\"%sapi/v2/problems/%s/render/", apiUrl, problemJid));
    }

    public static String replaceProblemEditorialRenderUrls(String text, String apiUrl, String problemJid) {
        return text
                .replaceAll(
                        "src=\"render/",
                        String.format("src=\"%sapi/v2/problems/%s/editorials/render/", apiUrl, problemJid))
                .replaceAll(
                        "url=render/",
                        String.format("url=%sapi/v2/problems/%s/editorials/render/", apiUrl, problemJid))
                .replaceAll(
                        "href=\"render/",
                        String.format("href=\"%sapi/v2/problems/%s/editorials/render/", apiUrl, problemJid));
    }

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
}
