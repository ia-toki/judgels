package org.iatoki.judgels.sandalphon.controllers.api.pub.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.problem.base.ProblemStore;
import org.iatoki.judgels.play.controllers.apis.AbstractJudgelsAPIController;
import play.db.jpa.Transactional;
import play.mvc.Http;
import play.mvc.Result;

@Singleton
public class PublicProblemAPIControllerV2 extends AbstractJudgelsAPIController {
    private final ProblemStore problemStore;

    @Inject
    public PublicProblemAPIControllerV2(
            ObjectMapper mapper,
            ProblemStore problemStore) {

        super(mapper);
        this.problemStore = problemStore;
    }

    @Transactional(readOnly = true)
    public Result renderStatementMedia(Http.Request req, String problemJid, String mediaFilename) {
        Problem problem = problemStore.findProblemByJid(problemJid);
        String mediaUrl = problemStore.getStatementMediaFileURL(null, problem.getJid(), mediaFilename);

        return okAsImage(req, mediaUrl);
    }

    @Transactional(readOnly = true)
    public Result renderEditorialMedia(Http.Request req, String problemJid, String mediaFilename) {
        Problem problem = problemStore.findProblemByJid(problemJid);
        String mediaUrl = problemStore.getEditorialMediaFileURL(null, problem.getJid(), mediaFilename);

        return okAsImage(req, mediaUrl);
    }
}
