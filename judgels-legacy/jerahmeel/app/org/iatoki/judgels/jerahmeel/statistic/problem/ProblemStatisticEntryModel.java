package org.iatoki.judgels.jerahmeel.statistic.problem;

import org.iatoki.judgels.play.model.AbstractModel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "jerahmeel_problem_statistic_entry")
public class ProblemStatisticEntryModel extends AbstractModel {

    @Id
    @GeneratedValue
    public long id;

    public String problemStatisticJid;

    public String problemJid;

    public long totalSubmissions;
}
