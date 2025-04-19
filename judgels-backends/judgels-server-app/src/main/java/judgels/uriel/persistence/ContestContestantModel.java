package judgels.uriel.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;
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
