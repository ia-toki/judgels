package judgels.sandalphon.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.persistence.UnmodifiableModel;

@Entity(name = "sandalphon_problem_tag")
@Table(indexes = {
        @Index(columnList = "problemJid,tag", unique = true),
        @Index(columnList = "tag")})
public class ProblemTagModel extends UnmodifiableModel {
    @Column(nullable = false)
    public String problemJid;

    @Column(nullable = false)
    public String tag;
}
