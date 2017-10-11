package judgels.jophiel.hibernate.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "jophiel_user")
public class UserModel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public long id;
    @Column public String jid;
    @Column public String username;
    @Column public String name;
    @Column public String email;
}
