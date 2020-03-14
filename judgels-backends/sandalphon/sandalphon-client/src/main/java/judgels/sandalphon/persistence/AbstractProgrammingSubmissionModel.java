package judgels.sandalphon.persistence;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import judgels.persistence.JidPrefix;
import judgels.persistence.JudgelsModel;

@SuppressWarnings("checkstyle:visibilitymodifier")
@MappedSuperclass
@JidPrefix("SUBM")
public abstract class AbstractProgrammingSubmissionModel extends JudgelsModel {
    @Column(nullable = false)
    public String problemJid;

    public String containerJid;

    @Column(nullable = false)
    public String gradingEngine;

    @Column(nullable = false)
    public String gradingLanguage;
}
