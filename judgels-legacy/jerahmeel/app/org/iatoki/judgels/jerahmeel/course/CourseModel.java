package org.iatoki.judgels.jerahmeel.course;

import judgels.persistence.JidPrefix;
import judgels.persistence.JudgelsModel;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "jerahmeel_course")
@JidPrefix("COUR")
public final class CourseModel extends JudgelsModel {

    public String name;

    @Column(columnDefinition = "TEXT")
    public String description;
}
