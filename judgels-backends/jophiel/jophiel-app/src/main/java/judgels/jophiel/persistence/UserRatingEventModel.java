package judgels.jophiel.persistence;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import judgels.persistence.UnmodifiableModel;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "jophiel_user_rating_event")
public class UserRatingEventModel extends UnmodifiableModel {
    @Column(nullable = false, unique = true)
    public Instant time;

    @Column(nullable = false)
    public String eventJid;
}
