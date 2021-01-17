package org.iatoki.judgels.sandalphon.problem.bundle.grading;

import java.util.List;
import java.util.Map;
import judgels.persistence.JudgelsDao;

public interface BaseBundleGradingDao<M extends AbstractBundleGradingModel> extends JudgelsDao<M> {

    M createGradingModel();

    Map<String, List<M>> getBySubmissionJids(List<String> submissionJids);
}
