package org.iatoki.judgels.jerahmeel.statistic.problem;

import judgels.persistence.Model;

import javax.persistence.Entity;

@Entity(name = "jerahmeel_problem_statistic_entry")
public class ProblemStatisticEntryModel extends Model {
    public String problemStatisticJid;

    public String problemJid;

    public long totalSubmissions;
}
