package org.iatoki.judgels.sandalphon.controllers.api.internal;

import static judgels.service.ServiceUtils.checkFound;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.problem.base.ProblemStore;
import judgels.sandalphon.problem.base.editorial.ProblemEditorialStore;
import org.iatoki.judgels.jophiel.controllers.Secured;
import org.iatoki.judgels.play.controllers.apis.AbstractJudgelsAPIController;
import play.db.jpa.Transactional;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

@Singleton
@Security.Authenticated(Secured.class)
public final class InternalProblemEditorialAPIController extends AbstractJudgelsAPIController {
    private final ProblemStore problemStore;
    private final ProblemEditorialStore editorialStore;

    @Inject
    public InternalProblemEditorialAPIController(ObjectMapper mapper, ProblemStore problemStore, ProblemEditorialStore editorialStore) {
        super(mapper);
        this.problemStore = problemStore;
        this.editorialStore = editorialStore;
    }

    @Transactional(readOnly = true)
    public Result renderMediaById(Http.Request req, long problemId, String mediaFilename) {
        String actorJid = req.attrs().get(Security.USERNAME);

        Problem problem = checkFound(problemStore.findProblemById(problemId));
        String mediaUrl = editorialStore.getEditorialMediaFileURL(actorJid, problem.getJid(), mediaFilename);

        return okAsImage(req, mediaUrl);
    }

    @Transactional(readOnly = true)
    public Result downloadEditorialMediaFile(Http.Request req, long id, String filename) {
        String actorJid = req.attrs().get(Security.USERNAME);

        Problem problem = checkFound(problemStore.findProblemById(id));
        String mediaUrl = editorialStore.getEditorialMediaFileURL(actorJid, problem.getJid(), filename);

        return okAsDownload(mediaUrl);
    }
}
