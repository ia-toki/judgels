package org.iatoki.judgels.sandalphon.problem.bundle.submission;

import judgels.persistence.JidPrefix;
import judgels.persistence.JudgelsModel;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
@JidPrefix("SUBM")
public abstract class AbstractBundleSubmissionModel extends JudgelsModel {

    public String problemJid;

    public String containerJid;
}
