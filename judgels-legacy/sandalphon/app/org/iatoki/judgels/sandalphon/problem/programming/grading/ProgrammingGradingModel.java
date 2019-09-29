package org.iatoki.judgels.sandalphon.problem.programming.grading;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity(name = "sandalphon_programming_grading")@Table(indexes = {
        @Index(columnList = "submissionJid"),
        @Index(columnList = "verdictCode")})
public final class ProgrammingGradingModel extends AbstractProgrammingGradingModel {
    public ProgrammingGradingModel() {}

    public ProgrammingGradingModel(String submissionJid, String verdictCode, String verdictName, int score) {
        this.submissionJid = submissionJid;
        this.verdictCode = verdictCode;
        this.verdictName = verdictName;
        this.score = score;
    }
}
