package org.iatoki.judgels.sandalphon.controllers.api.client.v2;

import com.fasterxml.jackson.databind.JsonNode;
import judgels.service.client.ClientChecker;
import org.iatoki.judgels.play.controllers.apis.AbstractJudgelsAPIController;
import org.iatoki.judgels.sandalphon.lesson.Lesson;
import org.iatoki.judgels.sandalphon.lesson.LessonService;
import org.iatoki.judgels.sandalphon.user.UserService;
import play.data.DynamicForm;
import play.db.jpa.Transactional;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public final class ClientLessonAPIControllerV2 extends AbstractJudgelsAPIController {
    private final ClientChecker clientChecker;
    private final UserService userService;
    private final LessonService lessonService;

    @Inject
    public ClientLessonAPIControllerV2(ClientChecker clientChecker, UserService userService, LessonService lessonService) {
        this.clientChecker = clientChecker;
        this.userService = userService;
        this.lessonService = lessonService;
    }

    @Transactional(readOnly = true)
    public Result translateAllowedSlugToJids() {
        authenticateAsJudgelsAppClient(clientChecker);

        String userJid = DynamicForm.form().bindFromRequest().get("userJid");

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

    private boolean isPartnerOrAbove(String userJid, Lesson lesson) {
        return lesson.getAuthorJid().equals(userJid)
            || lessonService.isUserPartnerForLesson(lesson.getJid(), userJid)
            || userService.findUserByJid(userJid).getRoles().contains("admin");
    }
}
