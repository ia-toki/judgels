package judgels.jophiel.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import judgels.persistence.Model;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "jophiel_user_reset_password")
@Table(indexes = {@Index(columnList = "userJid")})
public class UserResetPasswordModel extends Model {
    @Column(nullable = false)
    public String userJid;

    @Column(unique = true, nullable = false)
    public String emailCode;

    public boolean consumed;
}
