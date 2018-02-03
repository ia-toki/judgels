package judgels.uriel.contest;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.persistence.Model;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "uriel_scoreboard")
@Table(indexes = {@Index(columnList = "contestJid,isOfficial", unique = true)})
public class ContestScoreboardModel extends Model {
    @Column(nullable = false)
    public String contestJid;

    @Column(nullable = false)
    public boolean isOfficial;

    @Column(columnDefinition = "TEXT", nullable = false)
    public String scoreboard;
}
