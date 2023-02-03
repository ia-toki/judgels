package judgels.jerahmeel.chapter.lesson;

import static com.google.common.base.Preconditions.checkArgument;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jerahmeel.api.chapter.lesson.ChapterLesson;
import judgels.jerahmeel.api.chapter.lesson.ChapterLessonData;
import judgels.jerahmeel.api.chapter.lesson.ChapterLessonService;
import judgels.jerahmeel.api.chapter.lesson.ChapterLessonStatement;
import judgels.jerahmeel.api.chapter.lesson.ChapterLessonsResponse;
import judgels.jerahmeel.chapter.ChapterStore;
import judgels.jerahmeel.role.RoleChecker;
import judgels.sandalphon.api.lesson.LessonInfo;
import judgels.sandalphon.api.lesson.LessonStatement;
import judgels.sandalphon.lesson.LessonClient;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class ChapterLessonResource implements ChapterLessonService {
    private final ActorChecker actorChecker;
    private final RoleChecker roleChecker;
    private final ChapterStore chapterStore;
    private final ChapterLessonStore lessonStore;
    private final LessonClient lessonClient;

    @Inject
    public ChapterLessonResource(
            ActorChecker actorChecker,
            RoleChecker roleChecker,
            ChapterStore chapterStore,
            ChapterLessonStore lessonStore,
            LessonClient lessonClient) {

        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.chapterStore = chapterStore;
        this.lessonStore = lessonStore;
        this.lessonClient = lessonClient;
    }

    @Override
    @UnitOfWork
    public void setLessons(AuthHeader authHeader, String chapterJid, List<ChapterLessonData> data) {
        String actorJid = actorChecker.check(authHeader);
        checkFound(chapterStore.getChapterByJid(chapterJid));
        checkAllowed(roleChecker.isAdmin(actorJid));

        Set<String> aliases = data.stream().map(ChapterLessonData::getAlias).collect(Collectors.toSet());
        Set<String> slugs = data.stream().map(ChapterLessonData::getSlug).collect(Collectors.toSet());

        checkArgument(data.size() <= 100, "Cannot set more than 100 lessons.");
        checkArgument(aliases.size() == data.size(), "Lesson aliases must be unique");
        checkArgument(slugs.size() == data.size(), "Lesson slugs must be unique");

        Map<String, String> slugToJidMap = lessonClient.translateAllowedSlugsToJids(actorJid, slugs);

        List<ChapterLesson> setData = data.stream().filter(cp -> slugToJidMap.containsKey(cp.getSlug())).map(lesson ->
                new ChapterLesson.Builder()
                        .alias(lesson.getAlias())
                        .lessonJid(slugToJidMap.get(lesson.getSlug()))
                        .build())
                .collect(Collectors.toList());

        lessonStore.setLessons(chapterJid, setData);
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ChapterLessonsResponse getLessons(Optional<AuthHeader> authHeader, String chapterJid) {
        actorChecker.check(authHeader);
        checkFound(chapterStore.getChapterByJid(chapterJid));

        List<ChapterLesson> lessons = lessonStore.getLessons(chapterJid);
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

        actorChecker.check(authHeader);
        checkFound(chapterStore.getChapterByJid(chapterJid));

        ChapterLesson lesson = checkFound(lessonStore.getLessonByAlias(chapterJid, lessonAlias));
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
