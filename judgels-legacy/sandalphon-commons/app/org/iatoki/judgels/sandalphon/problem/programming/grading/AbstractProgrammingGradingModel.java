package org.iatoki.judgels.sandalphon.problem.programming.grading;

import judgels.persistence.JidPrefix;
import judgels.persistence.JudgelsModel;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@JidPrefix("GRAD")
public abstract class AbstractProgrammingGradingModel extends JudgelsModel {

    public String submissionJid;

    public String verdictCode;

    public String verdictName;

    public int score;

    @Column(columnDefinition = "LONGTEXT")
    public String details;
}
