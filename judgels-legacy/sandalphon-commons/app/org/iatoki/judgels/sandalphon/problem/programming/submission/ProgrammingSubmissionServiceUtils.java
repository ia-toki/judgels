package org.iatoki.judgels.sandalphon.problem.programming.submission;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import judgels.gabriel.api.Verdicts;
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
        return new ProgrammingSubmission(submissionModel.id, submissionModel.jid, submissionModel.problemJid, submissionModel.containerJid, submissionModel.createdBy, submissionModel.gradingEngine, submissionModel.gradingLanguage, new Date(submissionModel.createdAt.toEpochMilli()),
                Lists.transform(gradingModels, m -> createGradingFromModel(m))
        );
    }

    public static Grading createGradingFromModel(AbstractProgrammingGradingModel gradingModel) {
        return new Grading(gradingModel.id, gradingModel.jid, Verdicts.fromCode(gradingModel.verdictCode), gradingModel.score, gradingModel.details);
    }
}
