package org.iatoki.judgels.jerahmeel.chapter.lesson;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.persistence.Model;

@Entity(name = "jerahmeel_chapter_lesson")
@Table(indexes = {
        @Index(columnList = "chapterJid,lessonJid", unique = true),
        @Index(columnList = "chapterJid,alias", unique = true)})
public final class ChapterLessonModel extends Model {
    @Column(nullable = false)
    public String chapterJid;

    @Column(nullable = false)
    public String lessonJid;

    @Column(nullable = false)
    public String alias;

    @Column(nullable = false)
    public String status;
}
