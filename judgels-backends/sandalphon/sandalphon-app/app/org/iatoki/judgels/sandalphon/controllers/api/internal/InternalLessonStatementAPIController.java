package org.iatoki.judgels.sandalphon.controllers.api.internal;

import static judgels.service.ServiceUtils.checkFound;

import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.sandalphon.api.lesson.Lesson;
import org.iatoki.judgels.jophiel.controllers.Secured;
import org.iatoki.judgels.play.controllers.apis.AbstractJudgelsAPIController;
import org.iatoki.judgels.sandalphon.lesson.LessonService;
import play.db.jpa.Transactional;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

@Singleton
@Security.Authenticated(Secured.class)
public final class InternalLessonStatementAPIController extends AbstractJudgelsAPIController {
    private final LessonService lessonService;

    @Inject
    public InternalLessonStatementAPIController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @Transactional(readOnly = true)
    public Result renderMediaById(Http.Request req, long lessonId, String mediaFilename) {
        String actorJid = req.attrs().get(Security.USERNAME);

        Lesson lesson = checkFound(lessonService.findLessonById(lessonId));
        String mediaUrl = lessonService.getStatementMediaFileURL(actorJid, lesson.getJid(), mediaFilename);

        return okAsImage(mediaUrl);
    }

    @Transactional(readOnly = true)
    public Result downloadStatementMediaFile(Http.Request req, long id, String filename) {
        String actorJid = req.attrs().get(Security.USERNAME);

        Lesson lesson = checkFound(lessonService.findLessonById(id));
        String mediaUrl = lessonService.getStatementMediaFileURL(actorJid, lesson.getJid(), filename);

        return okAsDownload(mediaUrl);
    }
}
