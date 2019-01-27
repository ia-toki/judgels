package org.iatoki.judgels.jerahmeel.course.chapter;

import judgels.persistence.Model;

import javax.persistence.Entity;

@Entity(name = "jerahmeel_course_chapter")
public final class CourseChapterModel extends Model {
    public String courseJid;

    public String chapterJid;

    public String alias;
}
