package org.iatoki.judgels.sandalphon.problem.bundle.submission;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity(name = "sandalphon_bundle_submission")
@Table(indexes = {
        @Index(columnList = "problemJid,createdAt"),
        @Index(columnList = "problemJid,createdBy"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")})
public final class BundleSubmissionModel extends AbstractBundleSubmissionModel {

}
