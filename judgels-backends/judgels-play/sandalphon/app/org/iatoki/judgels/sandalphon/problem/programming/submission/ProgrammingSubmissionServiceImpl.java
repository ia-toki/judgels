package org.iatoki.judgels.sandalphon.problem.programming.submission;

import judgels.sealtiel.api.message.MessageService;
import judgels.service.api.client.BasicAuthHeader;
import org.iatoki.judgels.sandalphon.problem.programming.grading.ProgrammingGradingDao;
import org.iatoki.judgels.sandalphon.problem.programming.grading.ProgrammingGradingModel;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public final class ProgrammingSubmissionServiceImpl extends AbstractProgrammingSubmissionServiceImpl<ProgrammingSubmissionModel, ProgrammingGradingModel> implements ProgrammingSubmissionService {

    @Inject
    public ProgrammingSubmissionServiceImpl(ProgrammingSubmissionDao submissionDao, ProgrammingGradingDao programmingGradingDao, @Named("sealtiel") BasicAuthHeader sealtielClientAuthHeader, MessageService messageService, @Named("gabrielClientJid") String gabrielClientJid) {
        super(submissionDao, programmingGradingDao, sealtielClientAuthHeader, messageService, gabrielClientJid);
    }
}
