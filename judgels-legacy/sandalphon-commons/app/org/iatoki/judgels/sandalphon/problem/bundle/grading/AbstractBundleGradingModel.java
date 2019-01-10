package org.iatoki.judgels.sandalphon.problem.bundle.grading;

import org.iatoki.judgels.play.jid.JidPrefix;
import org.iatoki.judgels.play.model.AbstractJudgelsModel;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@JidPrefix("GRAD")
public abstract class AbstractBundleGradingModel extends AbstractJudgelsModel {

    public String submissionJid;

    public double score;

    @Column(columnDefinition = "LONGTEXT")
    public String details;
}
