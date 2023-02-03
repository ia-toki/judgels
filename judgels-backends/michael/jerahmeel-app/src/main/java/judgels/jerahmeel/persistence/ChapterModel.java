package judgels.jerahmeel.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.persistence.JidPrefix;
import judgels.persistence.JudgelsModel;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "jerahmeel_chapter")
@Table(indexes = {
        @Index(columnList = "name"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "updatedAt")})
@JidPrefix("SESS")
public final class ChapterModel extends JudgelsModel {
    @Column(nullable = false)
    public String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    public String description;
}
