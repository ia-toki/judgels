package org.iatoki.judgels.sandalphon.problem.programming.grading;

import judgels.sandalphon.persistence.AbstractProgrammingGradingModel;
import judgels.persistence.JudgelsDao;

import java.util.List;
import java.util.Map;

public interface BaseProgrammingGradingDao<M extends AbstractProgrammingGradingModel> extends JudgelsDao<M> {

    M createGradingModel();

    Map<String, List<M>> getBySubmissionJids(List<String> submissionJids);

    Map<String, M> getLatestBySubmissionJids(List<String> submissionJids);
}
