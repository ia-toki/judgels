package judgels.uriel.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.persistence.Model;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "uriel_contest_group_scoreboard")
@Table(indexes = {@Index(columnList = "contestGroupJid,type", unique = true)})
public class ContestGroupScoreboardModel extends Model {
    @Column(nullable = false)
    public String contestGroupJid;

    @Column(nullable = false)
    public String type;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    public String scoreboard;
}
