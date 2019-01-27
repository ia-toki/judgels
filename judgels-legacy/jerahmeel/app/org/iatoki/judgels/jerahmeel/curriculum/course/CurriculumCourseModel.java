package org.iatoki.judgels.jerahmeel.curriculum.course;

import judgels.persistence.Model;

import javax.persistence.Entity;

@Entity(name = "jerahmeel_curriculum_course")
public final class CurriculumCourseModel extends Model {
    public String curriculumJid;

    public String courseJid;

    public String alias;
}
