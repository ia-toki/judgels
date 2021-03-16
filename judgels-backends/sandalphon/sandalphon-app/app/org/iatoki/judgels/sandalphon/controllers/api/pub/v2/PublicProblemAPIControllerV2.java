package org.iatoki.judgels.sandalphon.controllers.api.pub.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.sandalphon.api.problem.Problem;
import org.iatoki.judgels.play.controllers.apis.AbstractJudgelsAPIController;
import org.iatoki.judgels.sandalphon.problem.base.ProblemStore;
import org.iatoki.judgels.sandalphon.problem.base.tag.ProblemTagStore;
import play.db.jpa.Transactional;
import play.mvc.Http;
import play.mvc.Result;

@Singleton
public class PublicProblemAPIControllerV2 extends AbstractJudgelsAPIController {
    private final ProblemStore problemStore;
    private final ProblemTagStore problemTagStore;

    @Inject
    public PublicProblemAPIControllerV2(
            ObjectMapper mapper,
            ProblemStore problemStore,
            ProblemTagStore problemTagStore) {

        super(mapper);
        this.problemStore = problemStore;
        this.problemTagStore = problemTagStore;
    }

    @Transactional
    public Result renameDefaultLanguageToId(Http.Request req, String problemJid) {
        problemStore.addStatementLanguage(null, problemJid, "id-ID");
        problemStore.makeStatementDefaultLanguage(null, problemJid, "id-ID");
        problemStore.disableStatementLanguage(null, problemJid, "en-US");
        problemStore.forceCommit("JIDUSERkFl4m7hVGs5JUF76aS8g", problemJid, "Fix statement language to Indonesian", "");
        return ok();
    }

    @Transactional
    public Result refreshProblemDerivedTags(Http.Request req, long lastProblemId, long limit) {
        return okAsJson(req, problemTagStore.refreshProblemDerivedTags(lastProblemId, limit));
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
