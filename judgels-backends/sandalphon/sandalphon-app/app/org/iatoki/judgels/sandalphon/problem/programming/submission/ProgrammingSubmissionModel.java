package org.iatoki.judgels.sandalphon.problem.programming.submission;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.sandalphon.persistence.AbstractProgrammingSubmissionModel;

@Entity(name = "sandalphon_programming_submission")
@Table(indexes = {
        @Index(columnList = "problemJid,createdBy"),
        @Index(columnList = "problemJid,gradingLanguage"),
        @Index(columnList = "problemJid,createdAt")})
public final class ProgrammingSubmissionModel extends AbstractProgrammingSubmissionModel {}
