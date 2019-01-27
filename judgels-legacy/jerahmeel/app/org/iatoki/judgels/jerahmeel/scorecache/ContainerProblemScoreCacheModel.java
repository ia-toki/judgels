package org.iatoki.judgels.jerahmeel.scorecache;

import judgels.persistence.Model;

import javax.persistence.Entity;

@Entity(name = "jerahmeel_container_problem_score_cache")
public final class ContainerProblemScoreCacheModel extends Model {
    public String containerJid;

    public String problemJid;

    public String userJid;

    public double score;
}
