package judgels.jerahmeel.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.persistence.UnmodifiableModel;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "jerahmeel_problem_contest")
@Table(indexes = {
        @Index(columnList = "problemJid,contestJid", unique = true)})
public final class ProblemContestModel extends UnmodifiableModel {
    @Column(nullable = false)
    public String problemJid;

    @Column(nullable = false)
    public String contestJid;
}
