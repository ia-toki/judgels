package judgels.sandalphon;

import java.util.Optional;
import judgels.sandalphon.api.lesson.LessonInfo;
import judgels.sandalphon.api.problem.ProblemInfo;

public class SandalphonUtils {
    private SandalphonUtils() {}

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

    public static String replaceProblemRenderUrls(String text, String baseUrl, String problemJid) {
        return text
                .replaceAll(
                        "src=\"render/",
                        String.format("src=\"%s/api/v2/problems/%s/render/", baseUrl, problemJid))
                .replaceAll(
                        "url=render/",
                        String.format("url=%s/api/v2/problems/%s/render/", baseUrl, problemJid))
                .replaceAll(
                        "href=\"render/",
                        String.format("href=\"%s/api/v2/problems/%s/render/", baseUrl, problemJid));
    }

    public static String replaceProblemEditorialRenderUrls(String text, String baseUrl, String problemJid) {
        return text
                .replaceAll(
                        "src=\"render/",
                        String.format("src=\"%s/api/v2/problems/%s/editorials/render/", baseUrl, problemJid))
                .replaceAll(
                        "url=render/",
                        String.format("url=%s/api/v2/problems/%s/editorials/render/", baseUrl, problemJid))
                .replaceAll(
                        "href=\"render/",
                        String.format("href=\"%s/api/v2/problems/%s/editorials/render/", baseUrl, problemJid));
    }

    public static String replaceLessonRenderUrls(String text, String baseUrl, String lessonJid) {
        return text
                .replaceAll(
                        "src=\"render/",
                        String.format("src=\"%s/api/v2/lessons/%s/render/", baseUrl, lessonJid))
                .replaceAll(
                        "url=render/",
                        String.format("url=%s/api/v2/lessons/%s/render/", baseUrl, lessonJid))
                .replaceAll(
                        "href=\"render/",
                        String.format("href=\"%s/api/v2/lessons/%s/render/", baseUrl, lessonJid));
    }
}
