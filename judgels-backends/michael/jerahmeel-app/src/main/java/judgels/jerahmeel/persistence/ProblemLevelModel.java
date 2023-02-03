package judgels.jerahmeel.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.persistence.Model;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "jerahmeel_problem_level")
@Table(indexes = {
        @Index(columnList = "problemJid,userJid", unique = true),
        @Index(columnList = "level")})
public final class ProblemLevelModel extends Model {
    @Column(nullable = false)
    public String problemJid;

    @Column(nullable = false)
    public String userJid;

    @Column(nullable = false)
    public int level;
}
