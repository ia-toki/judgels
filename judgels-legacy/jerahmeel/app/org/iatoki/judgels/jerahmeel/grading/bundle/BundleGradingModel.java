package org.iatoki.judgels.jerahmeel.grading.bundle;

import org.iatoki.judgels.sandalphon.problem.bundle.grading.AbstractBundleGradingModel;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity(name = "jerahmeel_bundle_grading")
@Table(indexes = {@Index(columnList = "submissionJid")})
public final class BundleGradingModel extends AbstractBundleGradingModel {

}
