package org.iatoki.judgels.sandalphon.controllers.api.client.v2;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.api.client.user.ClientUserService;
import judgels.sandalphon.api.lesson.Lesson;
import judgels.sandalphon.api.lesson.LessonInfo;
import judgels.sandalphon.api.lesson.LessonStatement;
import judgels.service.client.ClientChecker;
import org.iatoki.judgels.play.controllers.apis.AbstractJudgelsAPIController;
import org.iatoki.judgels.sandalphon.StatementLanguageStatus;
import org.iatoki.judgels.sandalphon.lesson.LessonService;
import play.db.jpa.Transactional;
import play.mvc.Result;
import play.mvc.Results;

@Singleton
public final class ClientLessonAPIControllerV2 extends AbstractJudgelsAPIController {
    private final ClientChecker clientChecker;
    private final ClientUserService userService;
    private final LessonService lessonService;

    @Inject
    public ClientLessonAPIControllerV2(ClientChecker clientChecker, ClientUserService userService, LessonService lessonService) {
        this.clientChecker = clientChecker;
        this.userService = userService;
        this.lessonService = lessonService;
    }

    @Transactional(readOnly = true)
    public Result getLesson(String lessonJid) throws IOException {
        authenticateAsJudgelsAppClient(clientChecker);

        if (!lessonService.lessonExistsByJid(lessonJid)) {
            return Results.notFound();
        }

        return okAsJson(getLessonInfo(lessonJid));
    }


    @Transactional(readOnly = true)
    public Result getLessonStatement(String lessonJid) throws IOException {
        authenticateAsJudgelsAppClient(clientChecker);

        if (!lessonService.lessonExistsByJid(lessonJid)) {
            return Results.notFound();
        }

        String language = sanitizeLanguageCode(lessonJid, formFactory.form().bindFromRequest().get("language"));

        LessonStatement statement = lessonService.getStatement(null, lessonJid, language);

        judgels.sandalphon.api.lesson.LessonStatement result = new judgels.sandalphon.api.lesson.LessonStatement.Builder()
                .title(statement.getTitle())
                .text(statement.getText())
                .build();

        return okAsJson(result);
    }

    @Transactional(readOnly = true)
    public Result findLessonsByJids() throws IOException {
        authenticateAsJudgelsAppClient(clientChecker);

        JsonNode lessonJids = request().body().asJson();

        Map<String, LessonInfo> result = new HashMap<>();

        for (JsonNode lessonJidNode : lessonJids) {
            String lessonJid = lessonJidNode.asText();
            if (lessonService.lessonExistsByJid(lessonJid)) {
                result.put(lessonJid, getLessonInfo(lessonJid));
            }
        }
        return okAsJson(result);
    }

    @Transactional(readOnly = true)
    public Result translateAllowedSlugToJids() {
        authenticateAsJudgelsAppClient(clientChecker);

        String userJid = formFactory.form().bindFromRequest().get("userJid");

        Map<String, String> result = new HashMap<>();

        JsonNode slugs = request().body().asJson();
        for (JsonNode slugNode : slugs) {
            String slug = slugNode.asText();
            if (!lessonService.lessonExistsBySlug(slug)) {
                continue;
            }
            Lesson lesson = lessonService.findLessonBySlug(slug);
            if (isPartnerOrAbove(userJid, lesson)) {
                result.put(slug, lesson.getJid());
            }
        }

        return okAsJson(result);
    }

    private LessonInfo getLessonInfo(String lessonJid) throws IOException {
        Lesson lesson = lessonService.findLessonByJid(lessonJid);

        LessonInfo.Builder res = new LessonInfo.Builder();
        res.slug(lesson.getSlug());
        res.defaultLanguage(simplifyLanguageCode(lessonService.getDefaultLanguage(null, lessonJid)));
        res.titlesByLanguage(lessonService.getTitlesByLanguage(null, lessonJid).entrySet()
                .stream()
                .collect(Collectors.toMap(e -> simplifyLanguageCode(e.getKey()), e -> e.getValue())));
        return res.build();
    }

    private boolean isPartnerOrAbove(String userJid, Lesson lesson) {
        return lesson.getAuthorJid().equals(userJid)
            || lessonService.isUserPartnerForLesson(lesson.getJid(), userJid)
            || userService.getUserRole(userJid).getSandalphon().orElse("").equals("ADMIN");
    }


    private String sanitizeLanguageCode(String lessonJid, String language) throws IOException {
        Map<String, StatementLanguageStatus> availableLanguages = lessonService.getAvailableLanguages(null, lessonJid);
        Map<String, String> simplifiedLanguages = availableLanguages.entrySet()
                .stream()
                .collect(Collectors.toMap(e -> simplifyLanguageCode(e.getKey()), e -> e.getKey()));

        if (!simplifiedLanguages.containsKey(language) || availableLanguages.get(simplifiedLanguages.get(language)) == StatementLanguageStatus.DISABLED) {
            language = simplifyLanguageCode(lessonService.getDefaultLanguage(null, lessonJid));
        }

        return simplifiedLanguages.get(language);
    }

    private static String simplifyLanguageCode(String code) {
        if (code.startsWith("zh")) {
            return code;
        }
        String[] tokens = code.split("-");
        if (tokens.length < 2) {
            return code;
        }
        return tokens[0];
    }
}
