package org.iatoki.judgels.jerahmeel.chapter.lesson;

import org.iatoki.judgels.play.EntityNotFoundException;

public final class ChapterLessonNotFoundException extends EntityNotFoundException {

    public ChapterLessonNotFoundException() {
        super();
    }

    public ChapterLessonNotFoundException(String s) {
        super(s);
    }

    public ChapterLessonNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChapterLessonNotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getEntityName() {
        return "Chapter Lesson";
    }
}
