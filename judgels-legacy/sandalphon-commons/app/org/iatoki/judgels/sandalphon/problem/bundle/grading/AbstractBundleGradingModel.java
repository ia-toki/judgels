package org.iatoki.judgels.sandalphon.problem.bundle.grading;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import judgels.persistence.JidPrefix;
import judgels.persistence.JudgelsModel;

@MappedSuperclass
@JidPrefix("GRAD")
public abstract class AbstractBundleGradingModel extends JudgelsModel {
    @Column(nullable = false)
    public String submissionJid;

    @Column(nullable = false)
    public int score;

    @Column(columnDefinition = "LONGTEXT")
    public String details;
}
