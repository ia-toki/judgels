package org.iatoki.judgels.jerahmeel.chapter.lesson;

import judgels.persistence.Model;

import javax.persistence.Entity;

@Entity(name = "jerahmeel_chapter_lesson")
public final class ChapterLessonModel extends Model {
    public String chapterJid;

    public String lessonJid;

    public String alias;

    public String status;
}
