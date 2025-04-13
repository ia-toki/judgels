package judgels.jophiel.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;
import judgels.persistence.UnmodifiableModel;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "jophiel_user_rating_event")
@Table(indexes = {@Index(columnList = "time,eventJid", unique = true)})
public class UserRatingEventModel extends UnmodifiableModel {
    @Column(nullable = false)
    public Instant time;

    @Column(nullable = false)
    public String eventJid;
}
