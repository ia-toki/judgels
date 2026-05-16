package judgels.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "sandalphon_lesson")
@Table(indexes = {
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy"),
        @Index(columnList = "updatedAt")})
@JidPrefix("LESS")
public final class LessonModel extends JudgelsModel {
    @Column(unique = true, nullable = false)
    public String slug;

    @Column(columnDefinition = "TEXT", nullable = false)
    public String additionalNote;
}
