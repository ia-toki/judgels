package org.iatoki.judgels.sandalphon.problem.programming.submission;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.iatoki.judgels.gabriel.Verdict;
import org.iatoki.judgels.sandalphon.problem.programming.grading.Grading;
import org.iatoki.judgels.sandalphon.problem.programming.grading.AbstractProgrammingGradingModel;

import java.util.Date;
import java.util.List;

public final class ProgrammingSubmissionServiceUtils {

    private ProgrammingSubmissionServiceUtils() {
        // prevent instantiation
    }

    public static ProgrammingSubmission createSubmissionFromModel(AbstractProgrammingSubmissionModel submissionModel) {
        return createSubmissionFromModels(submissionModel, ImmutableList.of());
    }

    public static ProgrammingSubmission createSubmissionFromModels(AbstractProgrammingSubmissionModel submissionModel, List<? extends AbstractProgrammingGradingModel> gradingModels) {
        return new ProgrammingSubmission(submissionModel.id, submissionModel.jid, submissionModel.problemJid, submissionModel.containerJid, submissionModel.userCreate, submissionModel.gradingEngine, submissionModel.gradingLanguage, new Date(submissionModel.timeCreate),
                Lists.transform(gradingModels, m -> createGradingFromModel(m))
        );
    }

    public static Grading createGradingFromModel(AbstractProgrammingGradingModel gradingModel) {
        return new Grading(gradingModel.id, gradingModel.jid, new Verdict(gradingModel.verdictCode, gradingModel.verdictName), gradingModel.score, gradingModel.details);
    }
}
