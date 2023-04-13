package judgels.sandalphon.persistence;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "sandalphon_programming_grading")
@Table(indexes = {
        @Index(columnList = "submissionJid"),
        @Index(columnList = "verdictCode")})
public final class ProgrammingGradingModel extends AbstractProgrammingGradingModel {
    public ProgrammingGradingModel() {}

    public ProgrammingGradingModel(
            long id,
            String jid,
            String submissionJid,
            String verdictCode,
            String verdictName,
            int score) {

        this.id = id;
        this.jid = jid;
        this.submissionJid = submissionJid;
        this.verdictCode = verdictCode;
        this.verdictName = verdictName;
        this.score = score;
    }
}
