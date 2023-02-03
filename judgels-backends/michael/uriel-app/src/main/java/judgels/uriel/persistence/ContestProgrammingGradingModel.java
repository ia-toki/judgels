package judgels.uriel.persistence;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.sandalphon.persistence.AbstractProgrammingGradingModel;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "uriel_contest_programming_grading")
@Table(indexes = {
        @Index(columnList = "submissionJid"),
        @Index(columnList = "verdictCode")})
public class ContestProgrammingGradingModel extends AbstractProgrammingGradingModel {
    public ContestProgrammingGradingModel() {}

    public ContestProgrammingGradingModel(
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
