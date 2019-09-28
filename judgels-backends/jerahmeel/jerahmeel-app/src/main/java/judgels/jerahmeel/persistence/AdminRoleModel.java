package judgels.jerahmeel.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import judgels.persistence.UnmodifiableModel;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "jerahmeel_role_admin")
public class AdminRoleModel extends UnmodifiableModel {
    @Column(unique = true, nullable = false)
    public String userJid;
}
