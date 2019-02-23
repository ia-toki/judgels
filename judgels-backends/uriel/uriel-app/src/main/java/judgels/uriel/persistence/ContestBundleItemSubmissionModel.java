package judgels.uriel.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.persistence.JidPrefix;
import judgels.persistence.JudgelsModel;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "uriel_contest_bundle_item_submission")
@Table(indexes = {@Index(columnList = "containerJid,createdBy,problemJid,itemJid", unique = true)})
@JidPrefix("SUBB")
public class ContestBundleItemSubmissionModel extends JudgelsModel {
    @Column(nullable = false)
    public String containerJid;

    @Column(nullable = false)
    public String problemJid;

    @Column(nullable = false)
    public String itemJid;

    @Column(nullable = false)
    public String answer;

    @Column(nullable = false)
    public String verdictCode;

    @Column(nullable = false)
    public String verdictName;

    @Column(nullable = true)
    public Integer score;
}
