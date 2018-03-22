package judgels.uriel.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import judgels.persistence.JidPrefix;
import judgels.persistence.JudgelsModel;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "uriel_contest")
@JidPrefix("CONT")
public class ContestModel extends JudgelsModel {
    @Column(nullable = false)
    public String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    public String description;

    @Column(nullable = false)
    public String style;
}
