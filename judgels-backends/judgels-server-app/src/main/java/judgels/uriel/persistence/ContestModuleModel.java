package judgels.uriel.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
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
