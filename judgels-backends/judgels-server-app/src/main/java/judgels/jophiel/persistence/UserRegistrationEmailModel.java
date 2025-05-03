package judgels.jophiel.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import judgels.persistence.Model;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "jophiel_user_registration_email")
public class UserRegistrationEmailModel extends Model {
    @Column(unique = true, nullable = false)
    public String userJid;

    @Column(unique = true, nullable = false)
    public String emailCode;

    public boolean verified;
}
