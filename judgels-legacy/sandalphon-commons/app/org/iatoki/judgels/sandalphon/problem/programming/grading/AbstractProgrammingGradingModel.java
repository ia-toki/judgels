package org.iatoki.judgels.sandalphon.problem.programming.grading;

import org.iatoki.judgels.play.jid.JidPrefix;
import org.iatoki.judgels.play.model.AbstractJudgelsModel;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@JidPrefix("GRAD")
public abstract class AbstractProgrammingGradingModel extends AbstractJudgelsModel {

    public String submissionJid;

    public String verdictCode;

    public String verdictName;

    public int score;

    @Column(columnDefinition = "LONGTEXT")
    public String details;
}
