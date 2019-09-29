package org.iatoki.judgels.jerahmeel.course.chapter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.persistence.Model;

@Entity(name = "jerahmeel_course_chapter")
@Table(indexes = {
        @Index(columnList = "courseJid,chapterJid", unique = true),
        @Index(columnList = "courseJid,alias", unique = true)})
public final class CourseChapterModel extends Model {
    @Column(nullable = false)
    public String courseJid;

    @Column(nullable = false)
    public String chapterJid;

    @Column(nullable = false)
    public String alias;
}
