package judgels.jophiel.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
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
