package org.iatoki.judgels.sandalphon.lesson;

import org.iatoki.judgels.play.jid.JidPrefix;
import org.iatoki.judgels.play.model.AbstractJudgelsModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "sandalphon_lesson")
@JidPrefix("LESS")
public final class LessonModel extends AbstractJudgelsModel {

    public String slug;

    @Column(columnDefinition = "TEXT")
    public String additionalNote;
}
