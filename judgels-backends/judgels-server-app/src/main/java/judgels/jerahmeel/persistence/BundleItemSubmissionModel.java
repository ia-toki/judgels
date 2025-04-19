package judgels.jerahmeel.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import judgels.sandalphon.persistence.AbstractBundleItemSubmissionModel;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "jerahmeel_bundle_item_submission")
@Table(indexes = {@Index(columnList = "containerJid,createdBy,problemJid,itemJid", unique = true)})
public class BundleItemSubmissionModel extends AbstractBundleItemSubmissionModel {}
