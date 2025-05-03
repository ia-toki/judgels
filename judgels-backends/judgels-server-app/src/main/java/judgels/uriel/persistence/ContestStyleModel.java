package judgels.uriel.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import judgels.persistence.Model;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "uriel_contest_style")
public class ContestStyleModel extends Model {
    @Column(unique = true, nullable = false)
    public String contestJid;

    @Column(columnDefinition = "TEXT", nullable = false)
    public String config;
}
