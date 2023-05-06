package judgels.sandalphon.lesson;

import static judgels.sandalphon.resource.LanguageUtils.simplifyLanguageCode;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.JudgelsAppConfiguration;
import judgels.sandalphon.SandalphonUtils;
import judgels.sandalphon.api.lesson.Lesson;
import judgels.sandalphon.api.lesson.LessonInfo;
import judgels.sandalphon.api.lesson.LessonStatement;
import judgels.sandalphon.lesson.statement.LessonStatementStore;
import judgels.sandalphon.resource.StatementLanguageStatus;
import judgels.sandalphon.role.RoleChecker;

public class LessonClient {
    private final JudgelsAppConfiguration appConfig;
    private final RoleChecker roleChecker;
    private final LessonStore lessonStore;
    private final LessonStatementStore statementStore;

    @Inject
    public LessonClient(
            JudgelsAppConfiguration appConfig,
            RoleChecker roleChecker,
            LessonStore lessonStore,
            LessonStatementStore statementStore) {

        this.appConfig = appConfig;
        this.roleChecker = roleChecker;
        this.lessonStore = lessonStore;
        this.statementStore = statementStore;
    }

    public Map<String, String> translateAllowedSlugsToJids(String actorJid, Set<String> slugs) {
        Optional<String> userJid = roleChecker.isAdmin(actorJid)
                ? Optional.empty()
                : Optional.of(actorJid);
        return lessonStore.translateAllowedSlugsToJids(userJid, slugs);
    }

    public LessonInfo getLesson(String lessonJid) {
        Lesson lesson = lessonStore.getLessonByJid(lessonJid).get();

        return new LessonInfo.Builder()
                .slug(lesson.getSlug())
                .defaultLanguage(simplifyLanguageCode(statementStore.getDefaultLanguage(null, lessonJid)))
                .titlesByLanguage(statementStore.getTitlesByLanguage(null, lessonJid).entrySet()
                        .stream()
                        .collect(Collectors.toMap(e -> simplifyLanguageCode(e.getKey()), e -> e.getValue())))
                .build();
    }

    public Map<String, LessonInfo> getLessons(Set<String> lessonJids) {
        return lessonJids.stream().collect(Collectors.toMap(jid -> jid, this::getLesson));
    }

    public LessonStatement getLessonStatement(String lessonJid, Optional<String> language) {
        String sanitizedLanguage = sanitizeLanguage(lessonJid, language);
        LessonStatement statement = statementStore.getStatement(null, lessonJid, sanitizedLanguage);

        return new LessonStatement.Builder()
                .from(statement)
                .text(SandalphonUtils.replaceLessonRenderUrls(statement.getText(), appConfig.getBaseUrl(), lessonJid))
                .build();
    }

    private String sanitizeLanguage(String problemJid, Optional<String> language) {
        Map<String, StatementLanguageStatus> availableLanguages = statementStore.getAvailableLanguages(null, problemJid);
        Map<String, String> simplifiedLanguages = availableLanguages.entrySet()
                .stream()
                .collect(Collectors.toMap(e -> simplifyLanguageCode(e.getKey()), e -> e.getKey()));

        String lang = language.orElse("");
        if (!simplifiedLanguages.containsKey(lang) || availableLanguages.get(simplifiedLanguages.get(lang)) == StatementLanguageStatus.DISABLED) {
            lang = simplifyLanguageCode(statementStore.getDefaultLanguage(null, problemJid));
        }

        return simplifiedLanguages.get(lang);
    }
}
