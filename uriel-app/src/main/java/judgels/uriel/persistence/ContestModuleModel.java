package judgels.uriel.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.persistence.Model;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "uriel_contest_module")
@Table(indexes = {@Index(columnList = "contestJid,name", unique = true)})
public class ContestModuleModel extends Model {
    @Column(nullable = false)
    public String contestJid;

    @Column(nullable = false)
    public String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    public String config;

    @Column(nullable = false)
    public boolean enabled;
}
