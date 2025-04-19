package judgels.jerahmeel.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import judgels.persistence.Model;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "jerahmeel_chapter_problem")
@Table(indexes = {
        @Index(columnList = "chapterJid,problemJid", unique = true),
        @Index(columnList = "chapterJid,alias", unique = true)})
public final class ChapterProblemModel extends Model {
    @Column(nullable = false)
    public String chapterJid;

    @Column(nullable = false)
    public String problemJid;

    @Column(nullable = false)
    public String alias;

    @Column(nullable = false)
    public String type;
}
