package judgels.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "jerahmeel_course")
@Table(indexes = {
        @Index(columnList = "name"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "updatedAt")})
@JidPrefix("COUR")
public class CourseModel extends JudgelsModel {
    @Column(unique = true)
    public String slug;

    @Column(nullable = false)
    public String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    public String description;
}
