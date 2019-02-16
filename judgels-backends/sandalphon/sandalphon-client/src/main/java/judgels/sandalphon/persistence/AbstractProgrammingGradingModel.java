package judgels.sandalphon.persistence;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import judgels.persistence.JidPrefix;
import judgels.persistence.JudgelsModel;

@SuppressWarnings("checkstyle:visibilitymodifier")
@MappedSuperclass
@JidPrefix("GRAD")
public abstract class AbstractProgrammingGradingModel extends JudgelsModel {
    @Column(nullable = false)
    public String submissionJid;

    @Column(nullable = false)
    public String verdictCode;

    @Column(nullable = false)
    public String verdictName;

    @Column(nullable = false)
    public int score;

    @Column(columnDefinition = "LONGTEXT")
    public String details;
}
