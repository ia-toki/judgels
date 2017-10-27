package judgels.jophiel.hibernate.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import judgels.model.JidPrefix;
import judgels.model.JudgelsModel;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "jophiel_user")
@JidPrefix("USER")
public class UserModel extends JudgelsModel {
    @Column(unique = true, nullable = false)
    public String email;

    @Column(unique = true, nullable = false)
    public String username;

    @Column(nullable = false)
    public String name;
}
