package org.iatoki.judgels.sandalphon.problem.bundle.submission;

import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleGradingDao;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleGradingModel;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleProblemGrader;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class BundleSubmissionServiceImpl extends AbstractBundleSubmissionServiceImpl<BundleSubmissionModel, BundleGradingModel> implements BundleSubmissionService {

    @Inject
    public BundleSubmissionServiceImpl(BundleSubmissionDao bundleSubmissionDao, BundleGradingDao bundleGradingDao, BundleProblemGrader bundleProblemGrader) {
        super(bundleSubmissionDao, bundleGradingDao, bundleProblemGrader);
    }
}
