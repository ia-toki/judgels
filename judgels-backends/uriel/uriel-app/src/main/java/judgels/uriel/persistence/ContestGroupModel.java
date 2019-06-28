package judgels.uriel.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import judgels.persistence.JidPrefix;
import judgels.persistence.JudgelsModel;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "uriel_contest_group")
@JidPrefix("CONG")
public class ContestGroupModel extends JudgelsModel {
    @Column(unique = true)
    public String slug;

    @Column(nullable = false)
    public String name;
}
