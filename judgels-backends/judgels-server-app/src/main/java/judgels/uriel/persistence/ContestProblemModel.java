package judgels.uriel.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.persistence.Model;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "uriel_contest_problem")
@Table(indexes = {
        @Index(columnList = "contestJid,problemJid", unique = true),
        @Index(columnList = "contestJid,alias", unique = true)})
public class ContestProblemModel extends Model {
    @Column(nullable = false)
    public String contestJid;

    @Column(nullable = false)
    public String problemJid;

    @Column(nullable = false)
    public String alias;

    @Column(nullable = false)
    public String status;

    @Column(nullable = false)
    public long submissionsLimit;

    @Column(nullable = false)
    public int points;
}
