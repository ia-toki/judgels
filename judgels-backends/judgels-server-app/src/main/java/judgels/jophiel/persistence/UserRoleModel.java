package judgels.jophiel.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import judgels.persistence.Model;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "jophiel_user_role")
public class UserRoleModel extends Model {
    @Column(unique = true, nullable = false)
    public String userJid;

    public String jophiel;

    public String sandalphon;

    public String uriel;

    public String jerahmeel;
}
