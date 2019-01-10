package org.iatoki.judgels.sandalphon.controllers.api.client.v1;

import org.iatoki.judgels.play.api.JudgelsAPIForbiddenException;
import org.iatoki.judgels.play.api.JudgelsAPIInternalServerErrorException;
import org.iatoki.judgels.play.controllers.apis.AbstractJudgelsAPIController;
import org.iatoki.judgels.sandalphon.lesson.Lesson;
import org.iatoki.judgels.sandalphon.client.ClientService;
import org.iatoki.judgels.sandalphon.client.lesson.ClientLesson;
import org.iatoki.judgels.sandalphon.controllers.api.object.v1.ClientLessonFindRequestV1;
import org.iatoki.judgels.sandalphon.controllers.api.object.v1.LessonV1;
import org.iatoki.judgels.sandalphon.lesson.LessonService;
import play.db.jpa.Transactional;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

@Singleton
public final class ClientLessonAPIControllerV1 extends AbstractJudgelsAPIController {

    private final ClientService clientService;
    private final LessonService lessonService;

    @Inject
    public ClientLessonAPIControllerV1(ClientService clientService, LessonService lessonService) {
        this.clientService = clientService;
        this.lessonService = lessonService;
    }

    @Transactional(readOnly = true)
    public Result findClientLesson() {
        authenticateAsJudgelsAppClient(clientService);
        ClientLessonFindRequestV1 requestBody = parseRequestBody(ClientLessonFindRequestV1.class);

        if (!clientService.clientExistsByJid(requestBody.clientJid)) {
            throw new JudgelsAPIForbiddenException("Client not recognized");
        }

        if (!lessonService.lessonExistsByJid(requestBody.lessonJid)) {
            throw new JudgelsAPIForbiddenException("Lesson not found");
        }

        if (!clientService.isClientAuthorizedForLesson(requestBody.lessonJid, requestBody.clientJid)) {
            throw new JudgelsAPIForbiddenException("Client not authorized to use lesson");
        }

        ClientLesson clientLesson = clientService.findClientLessonByClientJidAndLessonJid(requestBody.clientJid, requestBody.lessonJid);
        if (!clientLesson.getSecret().equals(requestBody.lessonSecret)) {
            throw new JudgelsAPIForbiddenException("Wrong client lesson credentials");
        }

        try {
            Lesson lesson = lessonService.findLessonByJid(requestBody.lessonJid);

            LessonV1 responseBody = new LessonV1();

            responseBody.jid = lesson.getJid();
            responseBody.slug = lesson.getSlug();
            responseBody.defaultLanguage = lessonService.getDefaultLanguage(null, requestBody.lessonJid);
            responseBody.titlesByLanguage = lessonService.getTitlesByLanguage(null, requestBody.lessonJid);

            return okAsJson(responseBody);
        } catch (IOException e) {
            throw new JudgelsAPIInternalServerErrorException(e);
        }
    }
}
