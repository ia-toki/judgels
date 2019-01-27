package org.iatoki.judgels.sandalphon.problem.programming.submission;

import judgels.persistence.JidPrefix;
import judgels.persistence.JudgelsModel;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
@JidPrefix("SUBM")
public abstract class AbstractProgrammingSubmissionModel extends JudgelsModel {

    public String problemJid;

    public String containerJid;

    public String gradingEngine;

    public String gradingLanguage;
}
