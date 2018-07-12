package judgels.jophiel.legacy.session;

import javax.persistence.Column;
import javax.persistence.Entity;
import judgels.persistence.UnmodifiableModel;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "jophiel_legacy_session")
public class LegacySessionModel extends UnmodifiableModel {
    @Column(unique = true, nullable = false)
    public String authCode;

    @Column(nullable = false)
    public String token;
}
