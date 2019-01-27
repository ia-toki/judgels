package org.iatoki.judgels.sandalphon.lesson;

import judgels.persistence.JidPrefix;
import judgels.persistence.JudgelsModel;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "sandalphon_lesson")
@JidPrefix("LESS")
public final class LessonModel extends JudgelsModel {

    public String slug;

    @Column(columnDefinition = "TEXT")
    public String additionalNote;
}
