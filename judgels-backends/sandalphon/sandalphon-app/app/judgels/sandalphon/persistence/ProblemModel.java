package judgels.sandalphon.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.persistence.JidChildPrefixes;
import judgels.persistence.JudgelsModel;

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
