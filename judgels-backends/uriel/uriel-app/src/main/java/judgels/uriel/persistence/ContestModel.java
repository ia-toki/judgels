package judgels.uriel.persistence;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import judgels.persistence.JidPrefix;
import judgels.persistence.JudgelsModel;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "uriel_contest")
@JidPrefix("CONT")
public class ContestModel extends JudgelsModel {
    @Column(unique = true)
    public String slug;

    @Column(nullable = false)
    public String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    public String description;

    @Column(nullable = false)
    public String style;

    @Column(nullable = false)
    public Instant beginTime;

    @Column(nullable = false)
    public long duration;
}
