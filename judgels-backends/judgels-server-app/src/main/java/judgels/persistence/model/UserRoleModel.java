package judgels.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import judgels.persistence.UnmodifiableModel;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "judgels_user_role")
@Table(indexes = {
        @Index(columnList = "userJid,role", unique = true),
        @Index(columnList = "role")})
public class UserRoleModel extends UnmodifiableModel {
    @Column(nullable = false)
    public String userJid;

    @Column(nullable = false)
    public String role;
}
