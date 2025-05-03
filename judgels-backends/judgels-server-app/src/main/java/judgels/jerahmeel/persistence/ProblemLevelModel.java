package judgels.jerahmeel.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
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
