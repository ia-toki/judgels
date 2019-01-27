package org.iatoki.judgels.jerahmeel.statistic.problemscore;

import judgels.persistence.Model;

import javax.persistence.Entity;
import java.time.Instant;

@Entity(name = "jerahmeel_problem_score_statistic_entry")
public class ProblemScoreStatisticEntryModel extends Model {
    public String problemScoreStatisticJid;

    public String userJid;

    public double score;

    public Instant time;
}
