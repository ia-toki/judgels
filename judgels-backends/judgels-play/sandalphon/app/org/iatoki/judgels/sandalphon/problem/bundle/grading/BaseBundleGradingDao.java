package org.iatoki.judgels.sandalphon.problem.bundle.grading;

import judgels.persistence.JudgelsDao;

import java.util.List;
import java.util.Map;

public interface BaseBundleGradingDao<M extends AbstractBundleGradingModel> extends JudgelsDao<M> {

    M createGradingModel();

    Map<String, List<M>> getBySubmissionJids(List<String> submissionJids);
}
