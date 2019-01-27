package org.iatoki.judgels.sandalphon.problem.bundle.grading;

import judgels.persistence.JidPrefix;
import judgels.persistence.JudgelsModel;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@JidPrefix("GRAD")
public abstract class AbstractBundleGradingModel extends JudgelsModel {

    public String submissionJid;

    public double score;

    @Column(columnDefinition = "LONGTEXT")
    public String details;
}
