package org.iatoki.judgels.sandalphon.problem.bundle.submission;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Date;
import java.util.List;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.AbstractBundleGradingModel;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleGrading;

public final class BundleSubmissionServiceUtils {

    private BundleSubmissionServiceUtils() {
        // prevent instantiation
    }

    public static BundleSubmission createSubmissionFromModel(AbstractBundleSubmissionModel submissionModel) {
        return createSubmissionFromModels(submissionModel, ImmutableList.of());
    }

    public static BundleSubmission createSubmissionFromModels(AbstractBundleSubmissionModel submissionModel, List<? extends AbstractBundleGradingModel> gradingModels) {
        return new BundleSubmission(submissionModel.id, submissionModel.jid, submissionModel.problemJid, submissionModel.containerJid, submissionModel.createdBy, new Date(submissionModel.createdAt.toEpochMilli()), submissionModel.createdIp,
                Lists.transform(gradingModels, m -> createGradingFromModel(m))
        );
    }

    public static BundleGrading createGradingFromModel(AbstractBundleGradingModel gradingModel) {
        return new BundleGrading(gradingModel.id, gradingModel.jid, gradingModel.score, gradingModel.details);
    }
}
