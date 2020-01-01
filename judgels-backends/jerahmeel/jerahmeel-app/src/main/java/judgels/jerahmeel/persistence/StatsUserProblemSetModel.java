package judgels.jerahmeel.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.persistence.Model;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "jerahmeel_stats_user_problem_set")
@Table(indexes = {
        @Index(columnList = "userJid,problemSetJid", unique = true),
        @Index(columnList = "problemSetJid,score")})
public class StatsUserProblemSetModel extends Model {
    @Column(nullable = false)
    public String userJid;

    @Column(nullable = false)
    public String problemSetJid;

    @Column(nullable = false)
    public int score;
}
