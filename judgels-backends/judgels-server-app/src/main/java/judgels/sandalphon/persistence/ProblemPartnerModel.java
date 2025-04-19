package judgels.sandalphon.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import judgels.persistence.Model;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "sandalphon_problem_partner")
@Table(indexes = {@Index(columnList = "problemJid,userJid", unique = true)})
public final class ProblemPartnerModel extends Model {
    @Column(nullable = false)
    public String problemJid;

    @Column(nullable = false)
    public String userJid;

    @Column(columnDefinition = "TEXT", nullable = false)
    public String baseConfig;

    @Column(columnDefinition = "TEXT", nullable = false)
    public String childConfig;
}
