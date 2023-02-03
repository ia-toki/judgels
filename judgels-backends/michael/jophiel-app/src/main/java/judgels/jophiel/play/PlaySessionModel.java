package judgels.jophiel.play;

import javax.persistence.Column;
import javax.persistence.Entity;
import judgels.persistence.UnmodifiableModel;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "jophiel_legacy_session")
public class PlaySessionModel extends UnmodifiableModel {
    @Column(unique = true, nullable = false)
    public String authCode;

    @Column(nullable = false)
    public String token;
}
