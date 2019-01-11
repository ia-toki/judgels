package org.iatoki.judgels.jerahmeel.statistic.problemscore;

import org.iatoki.judgels.play.jid.JidPrefix;
import org.iatoki.judgels.play.model.AbstractJudgelsModel;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "jerahmeel_problem_score_statistic")
@JidPrefix("PSST")
public class ProblemScoreStatisticModel extends AbstractJudgelsModel {

    public String problemJid;

    public long time;
}
