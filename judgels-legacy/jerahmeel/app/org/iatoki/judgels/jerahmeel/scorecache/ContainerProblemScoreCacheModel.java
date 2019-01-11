package org.iatoki.judgels.jerahmeel.scorecache;

import org.iatoki.judgels.play.model.AbstractModel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "jerahmeel_container_problem_score_cache")
public final class ContainerProblemScoreCacheModel extends AbstractModel {

    @Id
    @GeneratedValue
    public long id;

    public String containerJid;

    public String problemJid;

    public String userJid;

    public double score;
}
