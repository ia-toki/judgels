package org.iatoki.judgels.sandalphon.problem.programming.submission;

import org.iatoki.judgels.play.jid.JidPrefix;
import org.iatoki.judgels.play.model.AbstractJudgelsModel;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
@JidPrefix("SUBM")
public abstract class AbstractProgrammingSubmissionModel extends AbstractJudgelsModel {

    public String problemJid;

    public String containerJid;

    public String gradingEngine;

    public String gradingLanguage;
}
