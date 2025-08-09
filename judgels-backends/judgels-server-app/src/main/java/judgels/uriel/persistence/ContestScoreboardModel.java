package judgels.uriel.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
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
