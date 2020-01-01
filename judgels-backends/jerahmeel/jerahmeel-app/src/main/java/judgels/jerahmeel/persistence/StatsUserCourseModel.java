package judgels.jerahmeel.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.persistence.Model;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "jerahmeel_stats_user_course")
@Table(indexes = {
        @Index(columnList = "userJid,courseJid", unique = true),
        @Index(columnList = "courseJid,progress")})
public class StatsUserCourseModel extends Model {
    @Column(nullable = false)
    public String userJid;

    @Column(nullable = false)
    public String courseJid;

    @Column(nullable = false)
    public int progress;
}
