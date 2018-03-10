package judgels.uriel.role;

import javax.persistence.Column;
import javax.persistence.Entity;
import judgels.persistence.UnmodifiableModel;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "uriel_role_admin")
public class AdminRoleModel extends UnmodifiableModel {
    @Column(unique = true, nullable = false)
    public String userJid;
}
