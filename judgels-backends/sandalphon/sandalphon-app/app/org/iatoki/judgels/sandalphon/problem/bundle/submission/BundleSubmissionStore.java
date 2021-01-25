package org.iatoki.judgels.sandalphon.problem.bundle.submission;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.inject.Inject;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleGradingDao;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleGradingModel;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleProblemGrader;

public final class BundleSubmissionStore extends AbstractBundleSubmissionStore<BundleSubmissionModel, BundleGradingModel> {

    @Inject
    public BundleSubmissionStore(
            ObjectMapper mapper,
            BundleSubmissionDao bundleSubmissionDao,
            BundleGradingDao bundleGradingDao,
            BundleProblemGrader bundleProblemGrader) {
        super(mapper, bundleSubmissionDao, bundleGradingDao, bundleProblemGrader);
    }
}
