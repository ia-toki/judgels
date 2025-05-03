package judgels.uriel.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import judgels.persistence.JidPrefix;
import judgels.persistence.JudgelsModel;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "uriel_contest_clarification")
@Table(indexes = {@Index(columnList = "contestJid,createdBy")})
@JidPrefix("COCL")
public class ContestClarificationModel extends JudgelsModel {
    @Column(nullable = false)
    public String contestJid;

    @Column(nullable = false)
    public String topicJid;

    @Column(nullable = false)
    public String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    public String question;

    @Column(columnDefinition = "TEXT")
    public String answer;

    @Column(nullable = false)
    public String status;
}
