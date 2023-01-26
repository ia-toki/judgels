package org.iatoki.judgels.sandalphon.controllers.api.pub.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.sandalphon.api.lesson.Lesson;
import judgels.sandalphon.lesson.LessonStore;
import org.iatoki.judgels.play.controllers.apis.AbstractJudgelsAPIController;
import play.db.jpa.Transactional;
import play.mvc.Http;
import play.mvc.Result;

@Singleton
public class PublicLessonAPIControllerV2 extends AbstractJudgelsAPIController {
    private final LessonStore lessonStore;

    @Inject
    public PublicLessonAPIControllerV2(ObjectMapper mapper, LessonStore lessonStore) {
        super(mapper);
        this.lessonStore = lessonStore;
    }

    @Transactional(readOnly = true)
    public Result renderMedia(Http.Request req, String lessonJid, String mediaFilename) {
        Lesson lesson = lessonStore.findLessonByJid(lessonJid);
        String mediaUrl = lessonStore.getStatementMediaFileURL(null, lesson.getJid(), mediaFilename);

        return okAsImage(req, mediaUrl);
    }
}
