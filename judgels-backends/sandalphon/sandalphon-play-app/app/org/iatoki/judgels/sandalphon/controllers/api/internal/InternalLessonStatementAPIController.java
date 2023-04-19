package org.iatoki.judgels.sandalphon.controllers.api.internal;

import static judgels.service.ServiceUtils.checkFound;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.sandalphon.api.lesson.Lesson;
import judgels.sandalphon.lesson.LessonStore;
import judgels.sandalphon.lesson.statement.LessonStatementStore;
import org.iatoki.judgels.jophiel.controllers.Secured;
import org.iatoki.judgels.play.controllers.apis.AbstractJudgelsAPIController;
import play.db.jpa.Transactional;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

@Singleton
@Security.Authenticated(Secured.class)
public final class InternalLessonStatementAPIController extends AbstractJudgelsAPIController {
    private final LessonStore lessonStore;
    private final LessonStatementStore statementStore;

    @Inject
    public InternalLessonStatementAPIController(ObjectMapper mapper, LessonStore lessonStore, LessonStatementStore statementStore) {
        super(mapper);
        this.lessonStore = lessonStore;
        this.statementStore = statementStore;
    }

    @Transactional(readOnly = true)
    public Result renderMediaById(Http.Request req, long lessonId, String mediaFilename) {
        String actorJid = req.attrs().get(Security.USERNAME);

        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        String mediaUrl = statementStore.getStatementMediaFileURL(actorJid, lesson.getJid(), mediaFilename);

        return okAsImage(req, mediaUrl);
    }

    @Transactional(readOnly = true)
    public Result downloadStatementMediaFile(Http.Request req, long id, String filename) {
        String actorJid = req.attrs().get(Security.USERNAME);

        Lesson lesson = checkFound(lessonStore.findLessonById(id));
        String mediaUrl = statementStore.getStatementMediaFileURL(actorJid, lesson.getJid(), filename);

        return okAsDownload(mediaUrl);
    }
}
