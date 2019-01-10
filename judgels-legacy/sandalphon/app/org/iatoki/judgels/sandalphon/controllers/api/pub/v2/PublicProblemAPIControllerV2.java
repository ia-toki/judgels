package org.iatoki.judgels.sandalphon.controllers.api.pub.v2;

import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.controllers.apis.AbstractJudgelsAPIController;
import org.iatoki.judgels.sandalphon.problem.base.Problem;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import play.db.jpa.Transactional;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PublicProblemAPIControllerV2 extends AbstractJudgelsAPIController {
    private final ProblemService problemService;

    @Inject
    public PublicProblemAPIControllerV2(ProblemService problemService) {
        this.problemService = problemService;
    }

    @Transactional(readOnly = true)
    public Result renderMedia(String problemJid, String mediaFilename) {
        Problem problem = problemService.findProblemByJid(problemJid);
        String mediaUrl = problemService.getStatementMediaFileURL(IdentityUtils.getUserJid(), problem.getJid(), mediaFilename);

        return okAsImage(mediaUrl);
    }
}
