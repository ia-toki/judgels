package judgels.jophiel.session;

import javax.persistence.Column;
import javax.persistence.Entity;
import judgels.persistence.Model;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "jophiel_session")
public class SessionModel extends Model {
    @Column(unique = true, nullable = false)
    public String token;

    @Column(nullable = false)
    public String userJid;
}
