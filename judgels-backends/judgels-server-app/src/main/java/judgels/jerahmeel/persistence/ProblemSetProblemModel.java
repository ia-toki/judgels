package judgels.jerahmeel.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import judgels.persistence.Model;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "jerahmeel_problem_set_problem")
@Table(indexes = {
        @Index(columnList = "problemSetJid,problemJid", unique = true),
        @Index(columnList = "problemJid"),
        @Index(columnList = "problemSetJid,alias", unique = true)})
public final class ProblemSetProblemModel extends Model {
    @Column(nullable = false)
    public String problemSetJid;

    @Column(nullable = false)
    public String problemJid;

    @Column(nullable = false)
    public String alias;

    @Column(nullable = false)
    public String type;
}
