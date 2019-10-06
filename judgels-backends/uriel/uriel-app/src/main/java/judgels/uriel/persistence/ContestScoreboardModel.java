package judgels.uriel.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.persistence.Model;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "uriel_contest_scoreboard")
@Table(indexes = {@Index(columnList = "contestJid,type", unique = true)})
public class ContestScoreboardModel extends Model {
    @Column(nullable = false)
    public String contestJid;

    @Column(nullable = false)
    public String type;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    public String scoreboard;
}
