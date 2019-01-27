package org.iatoki.judgels.jerahmeel.curriculum;

import judgels.persistence.JidPrefix;
import judgels.persistence.JudgelsModel;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "jerahmeel_curriculum")
@JidPrefix("CURR")
public final class CurriculumModel extends JudgelsModel {

    public String name;

    @Column(columnDefinition = "TEXT")
    public String description;
}
