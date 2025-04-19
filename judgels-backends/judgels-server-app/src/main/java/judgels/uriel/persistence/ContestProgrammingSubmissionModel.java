package judgels.uriel.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import judgels.sandalphon.persistence.AbstractProgrammingSubmissionModel;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "uriel_contest_programming_submission")
@Table(indexes = {
        @Index(columnList = "containerJid,createdBy"),
        @Index(columnList = "containerJid,problemJid"),
        @Index(columnList = "containerJid,gradingLanguage"),
        @Index(columnList = "containerJid,createdAt")})
public class ContestProgrammingSubmissionModel extends AbstractProgrammingSubmissionModel {}
