package org.iatoki.judgels.sandalphon.problem.bundle.submission;

import javax.inject.Inject;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleGradingDao;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleGradingModel;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleProblemGrader;

public final class BundleSubmissionStore extends AbstractBundleSubmissionStore<BundleSubmissionModel, BundleGradingModel> {

    @Inject
    public BundleSubmissionStore(BundleSubmissionDao bundleSubmissionDao, BundleGradingDao bundleGradingDao, BundleProblemGrader bundleProblemGrader) {
        super(bundleSubmissionDao, bundleGradingDao, bundleProblemGrader);
    }
}
