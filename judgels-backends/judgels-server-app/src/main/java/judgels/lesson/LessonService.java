package judgels.lesson;

import static judgels.resource.LanguageUtils.simplifyLanguageCode;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.UriInfo;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import judgels.api.lesson.Lesson;
import judgels.api.lesson.LessonInfo;
import judgels.api.lesson.LessonStatement;
import judgels.lesson.statement.LessonStatementStore;
import judgels.resource.StatementLanguageStatus;
import judgels.role.ProblemAdminRoleChecker;

public class LessonService {
    @Inject protected ProblemAdminRoleChecker roleChecker;
    @Inject protected LessonStore lessonStore;
    @Inject protected LessonStatementStore lessonStatementStore;

    @Inject public LessonService() {}

    public Map<String, String> translateAllowedLessonSlugsToJids(String actorJid, Collection<String> slugs) {
        Optional<String> userJid = roleChecker.isAdmin(actorJid)
                ? Optional.empty()
                : Optional.of(actorJid);
        return lessonStore.translateAllowedSlugsToJids(userJid, slugs);
    }

    public LessonInfo getLesson(String lessonJid) {
        Lesson lesson = lessonStore.getLessonByJid(lessonJid).get();

        return new LessonInfo.Builder()
                .slug(lesson.getSlug())
                .defaultLanguage(simplifyLanguageCode(lessonStatementStore.getDefaultLanguage(null, lessonJid)))
                .titlesByLanguage(lessonStatementStore.getTitlesByLanguage(null, lessonJid).entrySet()
                        .stream()
                        .collect(Collectors.toMap(e -> simplifyLanguageCode(e.getKey()), e -> e.getValue())))
                .build();
    }

    public Map<String, LessonInfo> getLessons(Collection<String> lessonJids) {
        return Set.copyOf(lessonJids).stream().collect(Collectors.toMap(jid -> jid, this::getLesson));
    }

    public LessonStatement getLessonStatement(
            HttpServletRequest req,
            UriInfo uriInfo,
            String lessonJid, Optional<String> language) {

        String sanitizedLanguage = sanitizeLessonStatementLanguage(lessonJid, language);
        LessonStatement statement = lessonStatementStore.getStatement(null, lessonJid, sanitizedLanguage);
        String apiUrl = LessonUtils.getApiUrl(req, uriInfo);

        return new LessonStatement.Builder()
                .from(statement)
                .text(LessonUtils.replaceLessonRenderUrls(statement.getText(), apiUrl, lessonJid))
                .build();
    }

    private String sanitizeLessonStatementLanguage(String problemJid, Optional<String> language) {
        Map<String, StatementLanguageStatus> availableLanguages = lessonStatementStore.getAvailableLanguages(null, problemJid);
        Map<String, String> simplifiedLanguages = availableLanguages.entrySet()
                .stream()
                .collect(Collectors.toMap(e -> simplifyLanguageCode(e.getKey()), e -> e.getKey()));

        String lang = language.orElse("");
        if (!simplifiedLanguages.containsKey(lang) || availableLanguages.get(simplifiedLanguages.get(lang)) == StatementLanguageStatus.DISABLED) {
            lang = simplifyLanguageCode(lessonStatementStore.getDefaultLanguage(null, problemJid));
        }

        return simplifiedLanguages.get(lang);
    }
}
