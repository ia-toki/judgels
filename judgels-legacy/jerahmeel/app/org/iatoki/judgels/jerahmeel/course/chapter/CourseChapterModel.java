package org.iatoki.judgels.jerahmeel.course.chapter;

import org.iatoki.judgels.play.model.AbstractModel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "jerahmeel_course_chapter")
public final class CourseChapterModel extends AbstractModel {

    @Id
    @GeneratedValue
    public long id;

    public String courseJid;

    public String chapterJid;

    public String alias;
}
