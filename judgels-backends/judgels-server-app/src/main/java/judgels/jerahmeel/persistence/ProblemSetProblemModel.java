package judgels.jerahmeel.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
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
