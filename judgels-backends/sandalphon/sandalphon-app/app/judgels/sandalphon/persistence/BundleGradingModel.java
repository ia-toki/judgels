package judgels.sandalphon.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.persistence.JidPrefix;
import judgels.persistence.JudgelsModel;

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
