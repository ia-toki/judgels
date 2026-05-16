package judgels.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "jophiel_session")
@Table(indexes = {@Index(columnList = "userJid")})
public class SessionModel extends UnmodifiableModel {
    @Column(unique = true, nullable = false)
    public String token;

    @Column(nullable = false)
    public String userJid;
}
