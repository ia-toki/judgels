package judgels.sandalphon.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.persistence.UnmodifiableModel;

@Entity(name = "sandalphon_problem_setter")
@Table(indexes = {
        @Index(columnList = "problemJid,role,userJid", unique = true),
        @Index(columnList = "userJid,role")})
public class ProblemSetterModel extends UnmodifiableModel {
    @Column(nullable = false)
    public String problemJid;

    @Column(nullable = false)
    public String userJid;

    @Column(nullable = false)
    public String role;
}
