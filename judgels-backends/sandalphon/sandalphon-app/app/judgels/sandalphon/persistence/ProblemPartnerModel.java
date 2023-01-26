package judgels.sandalphon.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.persistence.Model;

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
