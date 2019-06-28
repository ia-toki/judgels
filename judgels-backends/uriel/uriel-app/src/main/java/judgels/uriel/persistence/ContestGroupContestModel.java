package judgels.uriel.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.persistence.Model;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "uriel_contest_group_contest")
@Table(indexes = {
        @Index(columnList = "contestGroupJid,contestJid", unique = true),
        @Index(columnList = "contestGroupJid,alias", unique = true)})
public class ContestGroupContestModel extends Model {
    @Column(nullable = false)
    public String contestGroupJid;

    @Column(nullable = false)
    public String contestJid;

    @Column(nullable = false)
    public String alias;
}
