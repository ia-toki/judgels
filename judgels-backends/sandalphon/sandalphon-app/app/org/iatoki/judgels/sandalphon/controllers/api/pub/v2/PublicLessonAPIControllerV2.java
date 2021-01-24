package org.iatoki.judgels.sandalphon.controllers.api.pub.v2;

import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.sandalphon.api.lesson.Lesson;
import org.iatoki.judgels.play.controllers.apis.AbstractJudgelsAPIController;
import org.iatoki.judgels.sandalphon.lesson.LessonStore;
import play.db.jpa.Transactional;
import play.mvc.Result;

@Singleton
public class PublicLessonAPIControllerV2 extends AbstractJudgelsAPIController {
    private final LessonStore lessonStore;

    @Inject
    public PublicLessonAPIControllerV2(LessonStore lessonStore) {
        this.lessonStore = lessonStore;
    }

    @Transactional(readOnly = true)
    public Result renderMedia(String lessonJid, String mediaFilename) {
        Lesson lesson = lessonStore.findLessonByJid(lessonJid);
        String mediaUrl = lessonStore.getStatementMediaFileURL(null, lesson.getJid(), mediaFilename);

        return okAsImage(mediaUrl);
    }
}
