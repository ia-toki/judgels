package org.iatoki.judgels.jerahmeel.statistic.problemscore;

import judgels.persistence.JidPrefix;
import judgels.persistence.JudgelsModel;

import javax.persistence.Entity;
import java.time.Instant;

@Entity(name = "jerahmeel_problem_score_statistic")
@JidPrefix("PSST")
public class ProblemScoreStatisticModel extends JudgelsModel {

    public String problemJid;

    public Instant time;
}
