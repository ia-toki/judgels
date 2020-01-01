package judgels.jerahmeel.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.persistence.Model;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "jerahmeel_stats_user_chapter")
@Table(indexes = {
        @Index(columnList = "userJid,chapterJid", unique = true),
        @Index(columnList = "chapterJid,progress")})
public class StatsUserChapterModel extends Model {
    @Column(nullable = false)
    public String userJid;

    @Column(nullable = false)
    public String chapterJid;

    @Column(nullable = false)
    public int progress;
}
