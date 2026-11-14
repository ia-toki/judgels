package judgels.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import judgels.persistence.Model;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "jophiel_user_registration_email")
public class AccountRegistrationEmailModel extends Model {
    @Column(unique = true, nullable = false)
    public String userJid;

    @Column(unique = true, nullable = false)
    public String emailCode;

    public boolean verified;
}
