package org.iatoki.judgels.sandalphon.problem.programming.submission;

import judgels.sealtiel.api.message.MessageService;
import judgels.service.api.client.BasicAuthHeader;
import org.iatoki.judgels.sandalphon.problem.programming.grading.ProgrammingGradingDao;
import org.iatoki.judgels.sandalphon.problem.programming.grading.ProgrammingGradingModel;
import org.iatoki.judgels.sandalphon.sealtiel.SealtielClientAuthHeader;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class ProgrammingSubmissionServiceImpl extends AbstractProgrammingSubmissionServiceImpl<ProgrammingSubmissionModel, ProgrammingGradingModel> implements ProgrammingSubmissionService {

    @Inject
    public ProgrammingSubmissionServiceImpl(ProgrammingSubmissionDao submissionDao, ProgrammingGradingDao programmingGradingDao, @SealtielClientAuthHeader BasicAuthHeader sealtielClientAuthHeader, MessageService messageService, @GabrielClientJid String gabrielClientJid) {
        super(submissionDao, programmingGradingDao, sealtielClientAuthHeader, messageService, gabrielClientJid);
    }
}
