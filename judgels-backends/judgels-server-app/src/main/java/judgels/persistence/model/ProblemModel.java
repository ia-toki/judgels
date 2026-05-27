package judgels.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import judgels.persistence.JidChildPrefixes;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "sandalphon_problem")
@Table(indexes = {
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy"),
        @Index(columnList = "updatedAt")})
@JidChildPrefixes({"PROG", "BUND"})
public final class ProblemModel extends JudgelsModel {
    @Column(unique = true, nullable = false)
    public String slug;

    @Column(columnDefinition = "TEXT", nullable = false)
    public String additionalNote;
}
