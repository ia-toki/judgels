package org.iatoki.judgels.jerahmeel.scorecache;

import judgels.persistence.Model;

import javax.persistence.Entity;

@Entity(name = "jerahmeel_container_score_cache")
public final class ContainerScoreCacheModel extends Model {
    public String containerJid;

    public String userJid;

    public double score;
}
