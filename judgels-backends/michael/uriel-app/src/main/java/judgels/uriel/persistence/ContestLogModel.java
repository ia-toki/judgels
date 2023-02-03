package judgels.uriel.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
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
