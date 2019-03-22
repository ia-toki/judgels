package judgels.uriel.api.contest.problem;

import static com.palantir.conjure.java.api.testing.Assertions.assertThat;
import static com.palantir.conjure.java.api.testing.Assertions.assertThatRemoteExceptionThrownBy;
import static java.util.Optional.of;
import static judgels.uriel.api.contest.problem.ContestProblemStatus.OPEN;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_HEADER;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_1_JID;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_1_SLUG;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_2_JID;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_2_SLUG;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_3_JID;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_3_SLUG;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.palantir.conjure.java.api.errors.ErrorType;
import java.util.List;
import java.util.Optional;
import judgels.gabriel.api.LanguageRestriction;
import judgels.sandalphon.api.problem.ProblemStatement;
import judgels.sandalphon.api.problem.bundle.EssayItemConfig;
import judgels.sandalphon.api.problem.bundle.Item;
import judgels.sandalphon.api.problem.bundle.ItemType;
import judgels.sandalphon.api.problem.bundle.MultipleChoiceItemConfig;
import judgels.sandalphon.api.problem.bundle.ShortAnswerItemConfig;
import judgels.sandalphon.api.problem.bundle.StatementItemConfig;
import judgels.sandalphon.api.problem.programming.ProblemLimits;
import judgels.sandalphon.api.problem.programming.ProblemSubmissionConfig;
import judgels.sandalphon.api.problem.programming.ProblemWorksheet;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestErrors;
import judgels.uriel.api.contest.problem.programming.ContestProblemWorksheet;
import org.junit.jupiter.api.Test;

class ContestProblemServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
    private ContestProblemService problemService = createService(ContestProblemService.class);

    @Test
    void end_to_end_flow() {
        Contest contest = createContestWithRoles("contest");

        // as manager
        problemService.setProblems(MANAGER_HEADER, contest.getJid(), ImmutableList.of(
                new ContestProblemData.Builder()
                        .alias("A")
                        .slug(PROBLEM_1_SLUG)
                        .status(OPEN)
                        .build()));

        List<ContestProblemData> data = ImmutableList.of(
                new ContestProblemData.Builder()
                        .alias("A")
                        .slug(PROBLEM_1_SLUG)
                        .status(OPEN)
                        .submissionsLimit(10)
                        .points(11)
                        .build(),
                new ContestProblemData.Builder()
                        .alias("B")
                        .slug("unknown-slug")
                        .status(OPEN)
                        .points(11)
                        .build(),
                new ContestProblemData.Builder()
                        .alias("C")
                        .slug(PROBLEM_2_SLUG)
                        .status(ContestProblemStatus.CLOSED)
                        .points(11)
                        .build());

        assertThatRemoteExceptionThrownBy(() -> problemService.setProblems(MANAGER_HEADER, contest.getJid(), data))
                .isGeneratedFromErrorType(ContestErrors.PROBLEM_SLUGS_NOT_ALLOWED);

        problemService.setProblems(MANAGER_HEADER, contest.getJid(), ImmutableList.of(
                new ContestProblemData.Builder()
                        .alias("A")
                        .slug(PROBLEM_1_SLUG)
                        .status(OPEN)
                        .submissionsLimit(10)
                        .points(11)
                        .build(),
                new ContestProblemData.Builder()
                        .alias("C")
                        .slug(PROBLEM_2_SLUG)
                        .status(ContestProblemStatus.CLOSED)
                        .points(23)
                        .build(),
                new ContestProblemData.Builder()
                        .alias("D")
                        .slug(PROBLEM_3_SLUG)
                        .status(ContestProblemStatus.OPEN)
                        .build()));

        ContestProblemsResponse response = problemService.getProblems(of(MANAGER_HEADER), contest.getJid());
        assertThat(response.getData()).containsOnly(
                new ContestProblem.Builder()
                        .alias("A")
                        .problemJid(PROBLEM_1_JID)
                        .status(OPEN)
                        .submissionsLimit(10)
                        .points(11)
                        .build(),
                new ContestProblem.Builder()
                        .alias("C")
                        .problemJid(PROBLEM_2_JID)
                        .status(ContestProblemStatus.CLOSED)
                        .points(23)
                        .build(),
                new ContestProblem.Builder()
                        .alias("D")
                        .problemJid(PROBLEM_3_JID)
                        .status(ContestProblemStatus.OPEN)
                        .build());
        assertThat(response.getProblemsMap().get(PROBLEM_1_JID).getSlug()).isEqualTo(PROBLEM_1_SLUG);
        assertThat(response.getTotalSubmissionsMap()).containsOnlyKeys(PROBLEM_1_JID, PROBLEM_2_JID, PROBLEM_3_JID);
        assertThat(response.getConfig().getCanManage()).isTrue();

        // as supervisor

        assertThatRemoteExceptionThrownBy(() -> problemService
                .setProblems(SUPERVISOR_HEADER, contest.getJid(), data))
                .isGeneratedFromErrorType(ErrorType.PERMISSION_DENIED);

        response = problemService.getProblems(of(SUPERVISOR_HEADER), contest.getJid());
        assertThat(response.getConfig().getCanManage()).isFalse();

        test_get_programming_problem_worksheet(contest);
        test_get_bundle_problem_worksheet(contest);
    }

    private void test_get_programming_problem_worksheet(Contest contest) {
        // as contestant

        ContestProblemWorksheet worksheet = problemService.getProgrammingProblemWorksheet(
                of(CONTESTANT_HEADER),
                contest.getJid(),
                "A",
                Optional.empty());
        assertThat(worksheet).isEqualTo(new ContestProblemWorksheet.Builder()
                .defaultLanguage("en")
                .languages(ImmutableSet.of("en"))
                .problem(new ContestProblem.Builder()
                        .alias("A")
                        .problemJid(PROBLEM_1_JID)
                        .status(OPEN)
                        .submissionsLimit(10)
                        .points(11)
                        .build())
                .totalSubmissions(0)
                .worksheet(new ProblemWorksheet.Builder()
                        .statement(new ProblemStatement.Builder()
                                .title("Problem 1")
                                .text("Statement for problem 1. <a href=\"http://localhost:9002/api/v2/problems/"
                                        + PROBLEM_1_JID + "/render/document\">link</a>")
                                .build())
                        .limits(new ProblemLimits.Builder()
                                .timeLimit(2000)
                                .memoryLimit(65536)
                                .build())
                        .submissionConfig(new ProblemSubmissionConfig.Builder()
                                .sourceKeys(ImmutableMap.of("source", "Source"))
                                .gradingEngine("Batch")
                                .gradingLanguageRestriction(LanguageRestriction.noRestriction())
                                .build())
                        .reasonNotAllowedToSubmit(Optional.empty())
                        .build())
                .build());

        // as non-contestant

        assertThatRemoteExceptionThrownBy(() -> problemService.getProgrammingProblemWorksheet(
                of(USER_HEADER),
                contest.getJid(),
                "A",
                Optional.empty()))
                .isGeneratedFromErrorType(ErrorType.PERMISSION_DENIED);
    }

    private void test_get_bundle_problem_worksheet(Contest contest) {
        judgels.uriel.api.contest.problem.bundle.ContestProblemWorksheet worksheet =
                new judgels.uriel.api.contest.problem.bundle.ContestProblemWorksheet.Builder()
                        .defaultLanguage("en")
                        .languages(ImmutableSet.of("en"))
                        .problem(new ContestProblem.Builder()
                                .alias("D")
                                .problemJid(PROBLEM_3_JID)
                                .status(OPEN)
                                .build())
                        .totalSubmissions(0)
                        .worksheet(new judgels.sandalphon.api.problem.bundle.ProblemWorksheet.Builder()
                                .statement(new ProblemStatement.Builder()
                                        .title("Problem 3")
                                        .text("<h3>Statement 3</h3> <img src=\"http://localhost:9002/api/v2/problems/"
                                                + PROBLEM_3_JID + "/render/image\"/>\r\n")
                                        .build())
                                .addItems(
                                        new Item.Builder()
                                                .jid("JIDITEMwcAjhP4KZurUE2F5LdSb")
                                                .type(ItemType.STATEMENT)
                                                .meta("1-2")
                                                .config(new StatementItemConfig.Builder()
                                                        .statement(
                                                                "<p>ini statement 1</p><img src=\"http://localhost:9002"
                                                                        + "/api/v2/problems/" + PROBLEM_3_JID
                                                                        + "/render/i\"/>")
                                                        .build())
                                                .build(),
                                        new Item.Builder()
                                                .jid("JIDITEMPeKuqUA0Q7zvJjTQXXVD")
                                                .type(ItemType.MULTIPLE_CHOICE)
                                                .number(1)
                                                .meta("1")
                                                .config(new MultipleChoiceItemConfig.Builder()
                                                        .statement("<p>ini soal 1</p>\r\n")
                                                        .score(1)
                                                        .penalty(0)
                                                        .addChoices(
                                                                new MultipleChoiceItemConfig.Choice.Builder()
                                                                        .alias("a")
                                                                        .content(
                                                                                "jawaban a <img src=\"http://localhost:9002"
                                                                                        + "/api/v2/problems/"
                                                                                        + PROBLEM_3_JID
                                                                                        + "/render/choiceimage\"/>")
                                                                        .build(),
                                                                new MultipleChoiceItemConfig.Choice.Builder()
                                                                        .alias("b")
                                                                        .content("jawaban b")
                                                                        .build()
                                                        )
                                                        .build())
                                                .build(),
                                        new Item.Builder()
                                                .jid("JIDITEMtOoiXuIgPcD1oUsMzvbP")
                                                .type(ItemType.MULTIPLE_CHOICE)
                                                .number(2)
                                                .meta("2")
                                                .config(new MultipleChoiceItemConfig.Builder()
                                                        .statement("<p>ini soal kedua</p>\r\n")
                                                        .score(4.0)
                                                        .penalty(-1.0)
                                                        .addChoices(
                                                                new MultipleChoiceItemConfig.Choice.Builder()
                                                                        .alias("a")
                                                                        .content("pilihan a")
                                                                        .build(),
                                                                new MultipleChoiceItemConfig.Choice.Builder()
                                                                        .alias("b")
                                                                        .content("pilihan b")
                                                                        .build()
                                                        )
                                                        .build())
                                                .build(),
                                        new Item.Builder()
                                                .jid("JIDITEMcD1oSDFJLadFSsMddfsf")
                                                .type(ItemType.SHORT_ANSWER)
                                                .number(3)
                                                .meta("3")
                                                .config(new ShortAnswerItemConfig.Builder()
                                                        .statement("<p>ini soal short answer</p>\r\n")
                                                        .score(4.0)
                                                        .penalty(-1.0)
                                                        .inputValidationRegex("\\d+")
                                                        .build())
                                                .build(),
                                        new Item.Builder()
                                                .jid("JIDITEMkhUulUkbUkYGBKYkfLHUh")
                                                .type(ItemType.ESSAY)
                                                .number(4)
                                                .meta("4")
                                                .config(new EssayItemConfig.Builder()
                                                        .statement("<p>buat program hello world</p>\r\n")
                                                        .score(12.0)
                                                        .build())
                                                .build()
                                )
                                .reasonNotAllowedToSubmit(Optional.empty())
                                .build())
                        .build();

        // as contestant

        judgels.uriel.api.contest.problem.bundle.ContestProblemWorksheet bw = problemService.getBundleProblemWorksheet(
                of(CONTESTANT_HEADER),
                contest.getJid(),
                "D",
                Optional.empty());
        assertThat(bw).isEqualTo(worksheet);

        // as non-contestant

        assertThatRemoteExceptionThrownBy(() -> problemService.getBundleProblemWorksheet(
                of(USER_HEADER),
                contest.getJid(),
                "D",
                Optional.empty()))
                .isGeneratedFromErrorType(ErrorType.PERMISSION_DENIED);
    }

}
