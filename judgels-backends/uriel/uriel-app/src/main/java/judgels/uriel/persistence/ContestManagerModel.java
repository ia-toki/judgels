package judgels.uriel.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.persistence.Model;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "uriel_contest_manager")
@Table(indexes = {@Index(columnList = "contestJid,userJid", unique = true)})
public class ContestManagerModel extends Model {
    @Column(nullable = false)
    public String contestJid;

    @Column(nullable = false)
    public String userJid;
}
