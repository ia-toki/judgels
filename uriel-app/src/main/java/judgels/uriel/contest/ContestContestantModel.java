package judgels.uriel.contest;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import judgels.persistence.Model;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "uriel_contest_contestant")
@Table(indexes = {@Index(columnList = "contestJid")},
        uniqueConstraints = {@UniqueConstraint(columnNames = {"contestJid", "userJid"})})
public class ContestContestantModel extends Model {
    @Column(nullable = false)
    public String contestJid;

    @Column(nullable = false)
    public String userJid;
}
