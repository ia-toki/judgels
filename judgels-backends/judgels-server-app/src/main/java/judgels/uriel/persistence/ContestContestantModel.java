package judgels.uriel.persistence;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.persistence.Model;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "uriel_contest_contestant")
@Table(indexes = {@Index(columnList = "contestJid,userJid", unique = true)})
public class ContestContestantModel extends Model {
    @Column(nullable = false)
    public String contestJid;

    @Column(nullable = false)
    public String userJid;

    @Column(nullable = false)
    public String status;

    public Instant contestStartTime;

    public Integer finalRank;
}
