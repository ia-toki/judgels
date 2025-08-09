package judgels.jerahmeel.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import judgels.persistence.Model;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "jerahmeel_stats_user_problem")
@Table(indexes = {
        @Index(columnList = "userJid,problemJid", unique = true),
        @Index(columnList = "problemJid,verdict"),
        @Index(columnList = "problemJid,score"),
        @Index(columnList = "problemJid,time"),
        @Index(columnList = "problemJid,memory")})
public class StatsUserProblemModel extends Model {
    @Column(nullable = false)
    public String userJid;

    @Column(nullable = false)
    public String problemJid;

    @Column(nullable = false)
    public String submissionJid;

    @Column(nullable = false)
    public String verdict;

    @Column(nullable = false)
    public int score;

    @Column(nullable = false)
    public int time;

    @Column(nullable = false)
    public int memory;
}
