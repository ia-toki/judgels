package org.iatoki.judgels.sandalphon.controllers.api.pub.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.gabriel.api.TestCase;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.engines.batch.BatchGradingConfig;
import judgels.gabriel.engines.batch.BatchWithSubtasksGradingConfig;
import judgels.gabriel.engines.interactive.InteractiveGradingConfig;
import judgels.gabriel.engines.interactive.InteractiveWithSubtasksGradingConfig;
import judgels.sandalphon.api.problem.Problem;
import org.iatoki.judgels.play.controllers.apis.AbstractJudgelsAPIController;
import org.iatoki.judgels.sandalphon.problem.base.ProblemStore;
import org.iatoki.judgels.sandalphon.problem.base.tag.ProblemTagStore;
import org.iatoki.judgels.sandalphon.problem.programming.ProgrammingProblemStore;
import play.db.jpa.Transactional;
import play.mvc.Http;
import play.mvc.Result;

@Singleton
public class PublicProblemAPIControllerV2 extends AbstractJudgelsAPIController {
    private final ProblemStore problemStore;
    private final ProgrammingProblemStore programmingProblemStore;
    private final ProblemTagStore problemTagStore;

    @Inject
    public PublicProblemAPIControllerV2(
            ObjectMapper mapper,
            ProblemStore problemStore,
            ProgrammingProblemStore programmingProblemStore,
            ProblemTagStore problemTagStore) {

        super(mapper);
        this.problemStore = problemStore;
        this.programmingProblemStore = programmingProblemStore;
        this.problemTagStore = problemTagStore;
    }

    @Transactional
    public Result refreshProblemDerivedTags(Http.Request req, long lastProblemId, long limit) {
        return okAsJson(req, problemTagStore.refreshProblemDerivedTags(lastProblemId, limit));
    }

    @Transactional
    public Result makePartialProblemAbsolute(Http.Request req, String problemJid) {
        String gradingEngine = programmingProblemStore.getGradingEngine(null, problemJid);
        if (gradingEngine.equals("Batch")) {
            return makeBatchPartialProblemAbsolute(req, problemJid);
        } else if (gradingEngine.equals("Interactive")) {
            return makeInteractivePartialProblemAbsolute(req, problemJid);
        }

        return forbidden();
    }

    private Result makeBatchPartialProblemAbsolute(Http.Request req, String problemJid) {
        BatchGradingConfig gradingConfig = (BatchGradingConfig) programmingProblemStore.getGradingConfig(null, problemJid);

        TestGroup sample = gradingConfig.getTestData().get(0);
        TestGroup official = gradingConfig.getTestData().get(1);

        TestGroup newSample = TestGroup.of(0, sample.getTestCases().stream()
                .map(tc -> TestCase.of(tc.getInput(), tc.getOutput(), ImmutableSet.of(0, 1)))
                .collect(Collectors.toList()));

        TestGroup newOfficial = TestGroup.of(1, official.getTestCases().stream()
                .filter(tc -> !existsInSample(sample, tc))
                .map(tc -> TestCase.of(tc.getInput(), tc.getOutput(), ImmutableSet.of(1)))
                .collect(Collectors.toList()));

        BatchWithSubtasksGradingConfig cfg = new BatchWithSubtasksGradingConfig.Builder()
                .from(gradingConfig)
                .subtaskPoints(ImmutableList.of(100))
                .testData(ImmutableList.of(newSample, newOfficial))
                .customScorer(gradingConfig.getCustomScorer())
                .build();

        programmingProblemStore.updateGradingEngine(null, problemJid, "BatchWithSubtasks");
        programmingProblemStore.updateGradingConfig(null, problemJid, cfg);
        problemStore.forceCommit("JIDUSERkFl4m7hVGs5JUF76aS8g", problemJid, "Change engine to BatchWithSubtasks", "");
        problemTagStore.refreshDerivedTags(problemJid);
        return ok();
    }

    private Result makeInteractivePartialProblemAbsolute(Http.Request req, String problemJid) {
        InteractiveGradingConfig gradingConfig = (InteractiveGradingConfig) programmingProblemStore.getGradingConfig(null, problemJid);

        TestGroup sample = gradingConfig.getTestData().get(0);
        TestGroup official = gradingConfig.getTestData().get(1);

        TestGroup newSample = TestGroup.of(0, sample.getTestCases().stream()
                .map(tc -> TestCase.of(tc.getInput(), tc.getOutput(), ImmutableSet.of(0, 1)))
                .collect(Collectors.toList()));

        TestGroup newOfficial = TestGroup.of(1, official.getTestCases().stream()
                .filter(tc -> !existsInSample(sample, tc))
                .map(tc -> TestCase.of(tc.getInput(), tc.getOutput(), ImmutableSet.of(1)))
                .collect(Collectors.toList()));

        InteractiveWithSubtasksGradingConfig cfg = new InteractiveWithSubtasksGradingConfig.Builder()
                .from(gradingConfig)
                .subtaskPoints(ImmutableList.of(100))
                .testData(ImmutableList.of(newSample, newOfficial))
                .communicator(gradingConfig.getCommunicator())
                .build();

        programmingProblemStore.updateGradingEngine(null, problemJid, "InteractiveWithSubtasks");
        programmingProblemStore.updateGradingConfig(null, problemJid, cfg);
        problemStore.forceCommit("JIDUSERkFl4m7hVGs5JUF76aS8g", problemJid, "Change engine to InteractiveWithSubtasks", "");
        problemTagStore.refreshDerivedTags(problemJid);
        return ok();
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

    private static boolean existsInSample(TestGroup sample, TestCase tc) {
        for (TestCase tcSample : sample.getTestCases()) {
            if (tcSample.getInput().equals(tc.getInput())) {
                return true;
            }
        }
        return false;
    }
}
