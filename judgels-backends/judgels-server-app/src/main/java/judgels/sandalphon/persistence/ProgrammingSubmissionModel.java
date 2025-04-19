package judgels.sandalphon.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "sandalphon_programming_submission")
@Table(indexes = {
        @Index(columnList = "problemJid,createdBy"),
        @Index(columnList = "problemJid,gradingLanguage"),
        @Index(columnList = "problemJid,createdAt")})
public final class ProgrammingSubmissionModel extends AbstractProgrammingSubmissionModel {}
