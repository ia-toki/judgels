package judgels.sandalphon.persistence;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import judgels.persistence.JidPrefix;
import judgels.persistence.JudgelsModel;

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
