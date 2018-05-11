package judgels.uriel.persistence;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.sandalphon.persistence.AbstractSubmissionModel;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "uriel_contest_programming_submission")
@Table(indexes = {@Index(columnList = "containerJid,createdBy,problemJid")})
public class ContestSubmissionModel extends AbstractSubmissionModel {}
