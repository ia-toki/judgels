package judgels.uriel.persistence;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.sandalphon.persistence.AbstractBundleItemSubmissionModel;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "uriel_contest_bundle_item_submission")
@Table(indexes = {@Index(columnList = "containerJid,createdBy,problemJid,itemJid", unique = true)})
public class ContestBundleItemSubmissionModel extends AbstractBundleItemSubmissionModel {}
