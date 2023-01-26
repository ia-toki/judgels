package judgels.sandalphon.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.persistence.JidPrefix;
import judgels.persistence.JudgelsModel;

@Entity(name = "sandalphon_bundle_submission")
@Table(indexes = {
        @Index(columnList = "problemJid,createdAt"),
        @Index(columnList = "problemJid,createdBy"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")})
@JidPrefix("SUBM")
public class BundleSubmissionModel extends JudgelsModel {
    @Column(nullable = false)
    public String problemJid;

    public String containerJid;
}
