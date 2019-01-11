package org.iatoki.judgels.jerahmeel.grading.programming;

import org.iatoki.judgels.sandalphon.problem.programming.grading.AbstractProgrammingGradingModel;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "jerahmeel_programming_grading")
public final class ProgrammingGradingModel extends AbstractProgrammingGradingModel {
    public ProgrammingGradingModel() {}

    public ProgrammingGradingModel(String submissionJid, String verdictCode, String verdictName, int score) {
        this.submissionJid = submissionJid;
        this.verdictCode = verdictCode;
        this.verdictName = verdictName;
        this.score = score;
    }
}
