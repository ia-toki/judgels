package org.iatoki.judgels.sandalphon.problem.bundle.grading;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity(name = "sandalphon_bundle_grading")
@Table(indexes = {@Index(columnList = "submissionJid")})
public final class BundleGradingModel extends AbstractBundleGradingModel {

}
