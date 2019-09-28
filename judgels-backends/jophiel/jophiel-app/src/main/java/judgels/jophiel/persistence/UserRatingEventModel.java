package judgels.jophiel.persistence;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
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
