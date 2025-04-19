package judgels.jophiel.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;
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
