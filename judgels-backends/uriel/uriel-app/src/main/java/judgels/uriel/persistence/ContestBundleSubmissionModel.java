package judgels.uriel.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.persistence.JidPrefix;
import judgels.persistence.JudgelsModel;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "uriel_contest_bundle_submission")
@Table(indexes = {@Index(columnList = "containerJid,problemJid,createdBy")})
@JidPrefix("SUBB")
public class ContestBundleSubmissionModel extends JudgelsModel {
    @Column(nullable = false)
    public String containerJid;

    @Column(nullable = false)
    public String problemJid;

    @Column(nullable = false)
    public String itemJid;

    @Column(nullable = false)
    public String value;
}
