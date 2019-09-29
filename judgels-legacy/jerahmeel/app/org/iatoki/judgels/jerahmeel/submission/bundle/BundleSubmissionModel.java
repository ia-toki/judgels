package org.iatoki.judgels.jerahmeel.submission.bundle;

import org.iatoki.judgels.sandalphon.problem.bundle.submission.AbstractBundleSubmissionModel;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity(name = "jerahmeel_bundle_submission")
@Table(indexes = {
        @Index(columnList = "containerJid,problemJid"),
        @Index(columnList = "containerJid,createdBy"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy"),
        @Index(columnList = "problemJid")})
public final class BundleSubmissionModel extends AbstractBundleSubmissionModel {

}
