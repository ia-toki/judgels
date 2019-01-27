package org.iatoki.judgels.jerahmeel.problemset.problem;

import judgels.persistence.Model;

import javax.persistence.Entity;

@Entity(name = "jerahmeel_problem_set_problem")
public final class ProblemSetProblemModel extends Model {
    public String problemSetJid;

    public String problemJid;

    public String alias;

    public String type;

    public String status;
}
