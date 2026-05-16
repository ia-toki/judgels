package judgels.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "jerahmeel_bundle_item_submission")
@Table(indexes = {@Index(columnList = "containerJid,createdBy,problemJid,itemJid", unique = true)})
public class BundleItemSubmissionModel extends AbstractBundleItemSubmissionModel {}
