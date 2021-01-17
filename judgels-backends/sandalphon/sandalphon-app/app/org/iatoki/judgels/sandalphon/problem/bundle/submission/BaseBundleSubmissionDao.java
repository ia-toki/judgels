package org.iatoki.judgels.sandalphon.problem.bundle.submission;

import java.time.Instant;
import java.util.List;
import judgels.persistence.JudgelsDao;

public interface BaseBundleSubmissionDao<M extends AbstractBundleSubmissionModel> extends JudgelsDao<M> {

    M createSubmissionModel();

    List<M> getByContainerJidAndUserJidAndProblemJid(String containerJid, String userJid, String problemJid);

    List<Instant> getAllSubmissionsSubmitTime();
}
