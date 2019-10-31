package judgels.jerahmeel.chapter.lesson;

import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jerahmeel.api.chapter.lesson.ChapterLesson;
import judgels.jerahmeel.api.chapter.lesson.ChapterLessonService;
import judgels.jerahmeel.api.chapter.lesson.ChapterLessonStatement;
import judgels.jerahmeel.api.chapter.lesson.ChapterLessonsResponse;
import judgels.jerahmeel.chapter.ChapterStore;
import judgels.sandalphon.api.lesson.LessonInfo;
import judgels.sandalphon.api.lesson.LessonStatement;
import judgels.sandalphon.lesson.LessonClient;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class ChapterLessonResource implements ChapterLessonService {
    private final ActorChecker actorChecker;
    private final ChapterStore chapterStore;
    private final ChapterLessonStore chapterLessonStore;
    private final LessonClient lessonClient;

    @Inject
    public ChapterLessonResource(
            ActorChecker actorChecker,
            ChapterStore chapterStore,
            ChapterLessonStore chapterLessonStore,
            LessonClient lessonClient) {

        this.actorChecker = actorChecker;
        this.chapterStore = chapterStore;
        this.chapterLessonStore = chapterLessonStore;
        this.lessonClient = lessonClient;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ChapterLessonsResponse getLessons(Optional<AuthHeader> authHeader, String chapterJid) {
        String actorJid = actorChecker.check(authHeader);
        checkFound(chapterStore.getChapterByJid(chapterJid));

        List<ChapterLesson> lessons = chapterLessonStore.getLessons(chapterJid);
        Set<String> lessonJids = lessons.stream().map(ChapterLesson::getLessonJid).collect(Collectors.toSet());
        Map<String, LessonInfo> lessonsMap = lessonClient.getLessons(lessonJids);

        return new ChapterLessonsResponse.Builder()
                .data(lessons)
                .lessonsMap(lessonsMap)
                .build();
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ChapterLessonStatement getLessonStatement(
            Optional<AuthHeader> authHeader,
            String chapterJid,
            String lessonAlias) {

        String actorJid = actorChecker.check(authHeader);
        checkFound(chapterStore.getChapterByJid(chapterJid));

        ChapterLesson lesson = checkFound(chapterLessonStore.getLessonByAlias(chapterJid, lessonAlias));
        String lessonJid = lesson.getLessonJid();
        LessonInfo lessonInfo = lessonClient.getLesson(lessonJid);
        LessonStatement statement = lessonClient.getLessonStatement(lesson.getLessonJid());

        return new ChapterLessonStatement.Builder()
                .defaultLanguage(lessonInfo.getDefaultLanguage())
                .languages(lessonInfo.getTitlesByLanguage().keySet())
                .lesson(lesson)
                .statement(statement)
                .build();
    }
}
