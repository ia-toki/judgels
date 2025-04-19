package judgels.jerahmeel.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;
import judgels.persistence.JidPrefix;
import judgels.persistence.JudgelsModel;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "jerahmeel_problem_set")
@Table(indexes = {
        @Index(columnList = "archiveJid"),
        @Index(columnList = "name"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "updatedAt")})
@JidPrefix("PRSE")
public final class ProblemSetModel extends JudgelsModel {
    @Column(unique = true)
    public String slug;

    @Column(nullable = false)
    public String archiveJid;

    @Column(nullable = false)
    public String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    public String description;

    @Column(nullable = false)
    public Instant contestTime;
}
