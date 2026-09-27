package judgels.lesson;

import jakarta.inject.Inject;
import judgels.api.lesson.Lesson;
import judgels.lesson.statement.LessonStatementStore;

public class LessonCreator {
    private final LessonStore lessonStore;
    private final LessonStatementStore statementStore;

    @Inject
    public LessonCreator(LessonStore lessonStore, LessonStatementStore statementStore) {
        this.lessonStore = lessonStore;
        this.statementStore = statementStore;
    }

    public Lesson createLesson(String actorJid, String slug, String additionalNote, String initialLanguage) {
        Lesson lesson = lessonStore.createLesson(slug, additionalNote);

        statementStore.initStatements(lesson.getJid(), initialLanguage);

        lessonStore.initRepository(actorJid, lesson.getJid());

        return lesson;
    }
}
