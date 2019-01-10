package org.iatoki.judgels.sandalphon.problem.bundle.submission;

import org.iatoki.judgels.play.jid.JidPrefix;
import org.iatoki.judgels.play.model.AbstractJudgelsModel;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
@JidPrefix("SUBM")
public abstract class AbstractBundleSubmissionModel extends AbstractJudgelsModel {

    public String problemJid;

    public String containerJid;
}
