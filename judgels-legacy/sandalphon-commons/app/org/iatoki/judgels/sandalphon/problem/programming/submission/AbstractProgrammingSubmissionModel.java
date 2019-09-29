package org.iatoki.judgels.sandalphon.problem.programming.submission;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import judgels.persistence.JidPrefix;
import judgels.persistence.JudgelsModel;

@MappedSuperclass
@JidPrefix("SUBM")
public abstract class AbstractProgrammingSubmissionModel extends JudgelsModel {
    @Column(nullable = false)
    public String problemJid;

    @Column(nullable = false)
    public String containerJid;

    @Column(nullable = false)
    public String gradingEngine;

    @Column(nullable = false)
    public String gradingLanguage;
}
