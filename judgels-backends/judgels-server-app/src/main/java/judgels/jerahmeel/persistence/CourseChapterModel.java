package judgels.jerahmeel.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import judgels.persistence.Model;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "jerahmeel_course_chapter")
@Table(indexes = {
        @Index(columnList = "courseJid,chapterJid", unique = true),
        @Index(columnList = "courseJid,alias", unique = true)})
public final class CourseChapterModel extends Model {
    @Column(nullable = false)
    public String courseJid;

    @Column(nullable = false)
    public String chapterJid;

    @Column(nullable = false)
    public String alias;
}
