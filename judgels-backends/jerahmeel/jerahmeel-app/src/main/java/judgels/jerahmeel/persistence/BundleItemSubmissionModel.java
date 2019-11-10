package judgels.jerahmeel.persistence;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.sandalphon.persistence.AbstractBundleItemSubmissionModel;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "jerahmeel_bundle_item_submission")
@Table(indexes = {@Index(columnList = "containerJid,createdBy,problemJid,itemJid", unique = true)})
public class BundleItemSubmissionModel extends AbstractBundleItemSubmissionModel {}
