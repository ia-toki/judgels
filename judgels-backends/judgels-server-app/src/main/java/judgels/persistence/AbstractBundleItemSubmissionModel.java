package judgels.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@SuppressWarnings("checkstyle:visibilitymodifier")
@MappedSuperclass
@JidPrefix("SUBB")
public abstract class AbstractBundleItemSubmissionModel extends JudgelsModel {
    @Column(nullable = false)
    public String containerJid;

    @Column(nullable = false)
    public String problemJid;

    @Column(nullable = false)
    public String itemJid;

    @Column(nullable = false)
    public String answer;

    @Column(nullable = false)
    public String verdict;

    @Column
    public Double score;
}
