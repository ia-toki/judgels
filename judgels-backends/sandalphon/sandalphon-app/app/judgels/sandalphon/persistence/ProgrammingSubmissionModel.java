package judgels.sandalphon.persistence;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity(name = "sandalphon_programming_submission")
@Table(indexes = {
        @Index(columnList = "problemJid,createdBy"),
        @Index(columnList = "problemJid,gradingLanguage"),
        @Index(columnList = "problemJid,createdAt")})
public final class ProgrammingSubmissionModel extends AbstractProgrammingSubmissionModel {}
