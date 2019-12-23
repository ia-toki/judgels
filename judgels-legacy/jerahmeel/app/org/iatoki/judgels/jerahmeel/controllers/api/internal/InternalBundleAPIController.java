package org.iatoki.judgels.jerahmeel.controllers.api.internal;

import judgels.jerahmeel.persistence.BundleItemSubmissionDao;
import judgels.jerahmeel.persistence.BundleItemSubmissionModel;
import judgels.persistence.JidGenerator;
import org.iatoki.judgels.FileSystemProvider;
import org.iatoki.judgels.jerahmeel.submission.bundle.BundleSubmissionLocalFileSystemProvider;
import org.iatoki.judgels.jerahmeel.submission.bundle.BundleSubmissionRemoteFileSystemProvider;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.controllers.apis.AbstractJudgelsAPIController;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleAnswer;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleDetailResult;
import org.iatoki.judgels.sandalphon.problem.bundle.submission.BundleSubmission;
import org.iatoki.judgels.sandalphon.problem.bundle.submission.BundleSubmissionService;
import org.iatoki.judgels.sandalphon.problem.bundle.submission.BundleSubmissionUtils;
import play.db.jpa.Transactional;
import play.mvc.Result;
import play.mvc.Results;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Map;

public class InternalBundleAPIController extends AbstractJudgelsAPIController {
    private final BundleSubmissionService service;

    private final FileSystemProvider bundleSubmissionLocalFileSystemProvider;
    private final FileSystemProvider bundleSubmissionRemoteFileSystemProvider;

    private final BundleItemSubmissionDao dao;

    @Inject
    public InternalBundleAPIController(BundleSubmissionService service, @BundleSubmissionLocalFileSystemProvider FileSystemProvider bundleSubmissionLocalFileSystemProvider, @BundleSubmissionRemoteFileSystemProvider @Nullable FileSystemProvider bundleSubmissionRemoteFileSystemProvider, BundleItemSubmissionDao dao) {
        this.service = service;
        this.bundleSubmissionLocalFileSystemProvider = bundleSubmissionLocalFileSystemProvider;
        this.bundleSubmissionRemoteFileSystemProvider = bundleSubmissionRemoteFileSystemProvider;
        this.dao = dao;
    }

    @Transactional
    public Result migrate() throws Exception {
        for (int pageIndex = 0;; pageIndex++) {
            Page<BundleSubmission> page = service.getPageOfBundleSubmissions(pageIndex, 1000, "id", "desc", null, null, null);
            if (page.getData().isEmpty()) {
                break;
            }

            System.out.println("Processing page " + pageIndex);

            for (BundleSubmission s : page.getData()) {
                Map<String, BundleDetailResult> results = BundleSubmissionUtils.parseGradingResult(s);
                BundleAnswer answers = service.createBundleAnswerFromPastSubmission(bundleSubmissionLocalFileSystemProvider, bundleSubmissionRemoteFileSystemProvider, s.getJid());
                for (Map.Entry<String, String> entry : answers.getAnswers().entrySet()) {
                    String itemJid = entry.getKey();
                    String answer = entry.getValue();
                    if (!results.containsKey(itemJid)) {
                        continue;
                    }

                    if (dao.selectByContainerJidAndProblemJidAndItemJidAndCreatedBy(s.getContainerJid(), s.getProblemJid(), itemJid, s.getAuthorJid()).isPresent()) {
                        continue;
                    }

                    double score = results.get(itemJid).getScore();

                    BundleItemSubmissionModel m = new BundleItemSubmissionModel();
                    m.containerJid = s.getContainerJid();
                    m.problemJid = s.getProblemJid();
                    m.itemJid = itemJid;
                    m.answer = answer;
                    m.verdict = score == 0.0 ? "WRONG_ANSWER" : "ACCEPTED";
                    m.score = score;

                    try {
                        dao.persist(m, s.getAuthorJid(), s.getTime().toInstant(), s.getIpAddress());
                    } catch (org.hibernate.exception.ConstraintViolationException e) {
                        // do nothing
                    }
                }
            }
        }

        return Results.ok();
    }
}
