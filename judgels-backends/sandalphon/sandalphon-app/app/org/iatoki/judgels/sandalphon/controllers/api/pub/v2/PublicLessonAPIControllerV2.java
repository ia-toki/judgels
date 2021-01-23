package org.iatoki.judgels.sandalphon.controllers.api.pub.v2;

import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.sandalphon.api.lesson.Lesson;
import org.iatoki.judgels.play.controllers.apis.AbstractJudgelsAPIController;
import org.iatoki.judgels.sandalphon.lesson.LessonService;
import play.db.jpa.Transactional;
import play.mvc.Result;

@Singleton
public class PublicLessonAPIControllerV2 extends AbstractJudgelsAPIController {
    private final LessonService lessonService;

    @Inject
    public PublicLessonAPIControllerV2(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @Transactional(readOnly = true)
    public Result renderMedia(String lessonJid, String mediaFilename) {
        Lesson lesson = lessonService.findLessonByJid(lessonJid);
        String mediaUrl = lessonService.getStatementMediaFileURL(null, lesson.getJid(), mediaFilename);

        return okAsImage(mediaUrl);
    }
}
