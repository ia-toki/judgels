package judgels.sandalphon.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import judgels.persistence.JidPrefix;
import judgels.persistence.JudgelsModel;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "sandalphon_bundle_grading")
@Table(indexes = {@Index(columnList = "submissionJid")})
@JidPrefix("GRAD")
public final class BundleGradingModel extends JudgelsModel {
    @Column(nullable = false)
    public String submissionJid;

    @Column(nullable = false)
    public int score;

    @Column(columnDefinition = "LONGTEXT")
    public String details;
}
