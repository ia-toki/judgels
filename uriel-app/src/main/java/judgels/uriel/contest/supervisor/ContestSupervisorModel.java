package judgels.uriel.contest.supervisor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.persistence.Model;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "uriel_contest_supervisor")
@Table(indexes = {@Index(columnList = "contestJid"),
                  @Index(columnList = "contestJid,userJid", unique = true)})
public class ContestSupervisorModel extends Model {
    @Column(nullable = false)
    public String contestJid;

    @Column(nullable = false)
    public String userJid;
}
