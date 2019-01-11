package org.iatoki.judgels.jerahmeel.statistic.problem;

import org.iatoki.judgels.play.jid.JidPrefix;
import org.iatoki.judgels.play.model.AbstractJudgelsModel;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "jerahmeel_problem_statistic")
@JidPrefix("PRST")
public class ProblemStatisticModel extends AbstractJudgelsModel {

    public long time;
}
