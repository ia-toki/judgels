package judgels.jophiel.persistence;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.persistence.UnmodifiableModel;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "jophiel_user_rating")
@Table(indexes = {
        @Index(columnList = "userJid"),
        @Index(columnList = "time,userJid", unique = true),
        @Index(columnList = "time,publicRating")})
public class UserRatingModel extends UnmodifiableModel {
    @Column(nullable = false)
    public Instant time;

    @Column(nullable = false)
    public String userJid;

    @Column(nullable = false)
    public int publicRating;

    @Column(nullable = false)
    public int hiddenRating;
}
