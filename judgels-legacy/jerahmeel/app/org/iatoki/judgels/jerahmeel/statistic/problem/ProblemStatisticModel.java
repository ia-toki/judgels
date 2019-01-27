package org.iatoki.judgels.jerahmeel.statistic.problem;

import judgels.persistence.JidPrefix;
import judgels.persistence.JudgelsModel;

import javax.persistence.Entity;
import java.time.Instant;

@Entity(name = "jerahmeel_problem_statistic")
@JidPrefix("PRST")
public class ProblemStatisticModel extends JudgelsModel {

    public Instant time;
}
