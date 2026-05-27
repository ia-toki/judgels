package judgels.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import judgels.persistence.JidPrefix;

@SuppressWarnings("checkstyle:visibilitymodifier")
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
