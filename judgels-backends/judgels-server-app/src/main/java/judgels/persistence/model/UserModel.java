package judgels.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import judgels.persistence.JidPrefix;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "jophiel_user")
@JidPrefix("USER")
public class UserModel extends JudgelsModel {
    @Column(unique = true, nullable = false)
    public String email;

    @Column(unique = true, nullable = false)
    public String username;

    @Column(nullable = false)
    public String password;

    public String avatarFilename;
}
