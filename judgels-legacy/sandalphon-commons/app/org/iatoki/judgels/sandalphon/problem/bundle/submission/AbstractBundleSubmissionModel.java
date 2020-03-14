package org.iatoki.judgels.sandalphon.problem.bundle.submission;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import judgels.persistence.JidPrefix;
import judgels.persistence.JudgelsModel;

@MappedSuperclass
@JidPrefix("SUBM")
public abstract class AbstractBundleSubmissionModel extends JudgelsModel {
    @Column(nullable = false)
    public String problemJid;

    public String containerJid;
}
