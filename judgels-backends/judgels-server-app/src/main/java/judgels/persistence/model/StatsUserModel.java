package judgels.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "jerahmeel_stats_user")
public class StatsUserModel extends Model {
    @Column(nullable = false, unique = true)
    public String userJid;

    @Column(nullable = false)
    public int score;
}
