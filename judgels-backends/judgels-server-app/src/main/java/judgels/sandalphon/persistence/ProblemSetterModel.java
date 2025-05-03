package judgels.sandalphon.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import judgels.persistence.UnmodifiableModel;

@SuppressWarnings("checkstyle:visibilitymodifier")
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
