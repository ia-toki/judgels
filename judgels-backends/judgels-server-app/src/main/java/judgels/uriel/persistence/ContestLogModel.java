package judgels.uriel.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import judgels.persistence.UnmodifiableModel;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "uriel_contest_log")
@Table(indexes = {
        @Index(columnList = "contestJid,createdBy"),
        @Index(columnList = "contestJid,event"),
        @Index(columnList = "contestJid,problemJid")})
public class ContestLogModel extends UnmodifiableModel {
    @Column(nullable = false)
    public String contestJid;

    @Column(nullable = false)
    public String event;

    public String object;

    public String problemJid;
}
