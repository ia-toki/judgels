package judgels.jerahmeel.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import judgels.sandalphon.persistence.AbstractProgrammingSubmissionModel;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "jerahmeel_programming_submission")
@Table(indexes = {
        @Index(columnList = "createdBy"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "problemJid"),
        @Index(columnList = "gradingLanguage"),
        @Index(columnList = "containerJid,createdBy"),
        @Index(columnList = "containerJid,problemJid"),
        @Index(columnList = "containerJid,gradingLanguage"),
        @Index(columnList = "containerJid,createdAt")})
public class ProgrammingSubmissionModel extends AbstractProgrammingSubmissionModel {}
