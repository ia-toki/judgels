package judgels.sandalphon.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.persistence.JidPrefix;
import judgels.persistence.JudgelsModel;

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
