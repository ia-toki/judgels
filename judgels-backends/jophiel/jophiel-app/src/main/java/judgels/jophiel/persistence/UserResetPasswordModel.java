package judgels.jophiel.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
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
