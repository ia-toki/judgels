package org.iatoki.judgels.sandalphon.problem.programming.submission;

import org.iatoki.judgels.play.model.JudgelsDao;

import java.util.List;

public interface BaseProgrammingSubmissionDao<M extends AbstractProgrammingSubmissionModel> extends JudgelsDao<M> {

    M createSubmissionModel();

    List<M> getByContainerJidSinceTime(String containerJid, long time);

    List<M> getByContainerJidAndUserJidAndProblemJid(String containerJid, String userJid, String problemJid);

    long countByContainerJidAndUserJidAndProblemJid(String containerJid, String userJid, String problemJid);

    List<Long> getAllSubmissionsSubmitTime();
}
