package org.iatoki.judgels.jerahmeel.submission.programming;

import org.iatoki.judgels.sandalphon.problem.programming.submission.AbstractProgrammingSubmissionModel;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

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
public final class ProgrammingSubmissionModel extends AbstractProgrammingSubmissionModel {

}
