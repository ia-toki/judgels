package judgels.uriel.api;

import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static judgels.uriel.api.contest.problem.ContestProblemStatus.OPEN;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.ws.rs.core.Form;
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
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestErrors;
import judgels.uriel.api.contest.problem.ContestProblem;
import judgels.uriel.api.contest.problem.ContestProblemData;
import judgels.uriel.api.contest.problem.ContestProblemStatus;
import judgels.uriel.api.contest.problem.programming.ContestProblemWorksheet;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestProblemApiIntegrationTests extends BaseUrielApiIntegrationTests {
    private Contest contest;

    @BeforeEach
    void before() {
        contest = buildContestWithRoles()
                .begun()
                .supervisorWithManagementPermissions(SUPERVISOR_A, SupervisorManagementPermission.PROBLEM)
                .supervisors(SUPERVISOR_B)
                .modules(REGISTRATION)
                .build();
    }

    @Test
    void set_get_problems() {
        problemClient.setProblems(managerToken, contest.getJid(), List.of(
                new ContestProblemData.Builder()
                        .alias("A")
                        .slug(PROBLEM_1_SLUG)
                        .status(OPEN)
                        .build()));

        problemClient.setProblems(managerToken, contest.getJid(), List.of(
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

        Map<String, Boolean> canManageMap = new LinkedHashMap<>();
        canManageMap.put(adminToken, true);
        canManageMap.put(managerToken, true);
        canManageMap.put(supervisorAToken, true);
        canManageMap.put(supervisorBToken, false);
        canManageMap.put(contestantToken, false);
        canManageMap.put(userToken, false);
        canManageMap.put("", false);

        for (String token : canManageMap.keySet()) {
            var response = problemClient.getProblems(token, contest.getJid());
            assertThat(response.getData()).containsOnly(
                    new ContestProblem.Builder()
                            .alias("A")
                            .problemJid(problem1.getJid())
                            .status(OPEN)
                            .submissionsLimit(10)
                            .points(11)
                            .build(),
                    new ContestProblem.Builder()
                            .alias("C")
                            .problemJid(problem2.getJid())
                            .status(ContestProblemStatus.CLOSED)
                            .points(23)
                            .build(),
                    new ContestProblem.Builder()
                            .alias("D")
                            .problemJid(problem3.getJid())
                            .status(ContestProblemStatus.OPEN)
                            .build());
            assertThat(response.getProblemsMap().get(problem1.getJid()).getSlug()).contains(PROBLEM_1_SLUG);
            assertThat(response.getTotalSubmissionsMap()).containsOnlyKeys(problem1.getJid(), problem2.getJid(), problem3.getJid());
            assertThat(response.getConfig().getCanManage()).isEqualTo(canManageMap.get(token));
        }
    }

    @Test
    void set_problems__forbidden() {
        List<ContestProblemData> data = List.of(
                new ContestProblemData.Builder()
                        .alias("A")
                        .slug(PROBLEM_1_SLUG)
                        .status(OPEN)
                        .build(),
                new ContestProblemData.Builder()
                        .alias("B")
                        .slug("unknown-slug")
                        .status(OPEN)
                        .points(11)
                        .build());

        assertForbidden(() -> problemClient.setProblems(managerToken, contest.getJid(), data))
                .hasMessageContaining(ContestErrors.PROBLEM_SLUGS_NOT_ALLOWED);
    }

    @Test
    void get_programming_problem_worksheet() {
        updateProblemStatement(managerToken, problem1,
                "Problem 1",
                "Statement 1. <img src=\"render/image.png\"/>");

        problemClient.setProblems(managerToken, contest.getJid(), List.of(
                new ContestProblemData.Builder()
                        .alias("A")
                        .slug(PROBLEM_1_SLUG)
                        .status(OPEN)
                        .submissionsLimit(10)
                        .points(11)
                        .build()));

        ContestProblemWorksheet worksheet = problemClient.getProgrammingProblemWorksheet(
                contestantToken,
                contest.getJid(),
                "A");
        assertThat(worksheet).usingRecursiveComparison().ignoringFieldsOfTypes(Instant.class).isEqualTo(new ContestProblemWorksheet.Builder()
                .defaultLanguage("en")
                .languages(Set.of("en"))
                .problem(new ContestProblem.Builder()
                        .alias("A")
                        .problemJid(problem1.getJid())
                        .status(OPEN)
                        .submissionsLimit(10)
                        .points(11)
                        .build())
                .totalSubmissions(0)
                .worksheet(new ProblemWorksheet.Builder()
                        .statement(new ProblemStatement.Builder()
                                .title("Problem 1")
                                .text("Statement 1. <img src=\"http://localhost:8080/api/v2/problems/" + problem1.getJid() + "/render/image.png\"/>")
                                .build())
                        .limits(new ProblemLimits.Builder()
                                .timeLimit(2000)
                                .memoryLimit(262144)
                                .build())
                        .submissionConfig(new ProblemSubmissionConfig.Builder()
                                .sourceKeys(Map.of("source", "Source code"))
                                .gradingEngine("Batch")
                                .gradingLanguageRestriction(LanguageRestriction.noRestriction())
                                .gradingLastUpdateTime(Instant.now()) // ignored
                                .build())
                        .reasonNotAllowedToSubmit(Optional.empty())
                        .build())
                .build());
    }

    @Test
    void get_bundle_problem_worksheet() {
        updateProblemStatement(managerToken, problem3,
                "Problem 3",
                "Statement 3. <img src=\"render/image.png\"/>");

        Form form = new Form();
        form.param("meta", "1-2");
        form.param("statement", "<p>STATEMENT 1-2</p> <img src=\"render/statement.png\"/>");
        String item1Jid = createBundleProblemItem(managerToken, problem3, ItemType.STATEMENT, form);

        form = new Form();
        form.param("meta", "1");
        form.param("statement", "<p>QUESTION 1</p>");
        form.param("score", "1");
        form.param("penalty", "0");
        form.param("choiceAliases", "a");
        form.param("choiceContents", "answer a <img src=\"render/a.png\"/>");
        form.param("choiceAliases", "b");
        form.param("choiceContents", "answer b");
        String item2Jid = createBundleProblemItem(managerToken, problem3, ItemType.MULTIPLE_CHOICE, form);

        form = new Form();
        form.param("meta", "2");
        form.param("statement", "<p>QUESTION 2</p>");
        form.param("score", "4");
        form.param("penalty", "-1");
        form.param("choiceAliases", "a");
        form.param("choiceContents", "answer a");
        form.param("choiceAliases", "b");
        form.param("choiceContents", "answer b");
        String item3Jid = createBundleProblemItem(managerToken, problem3, ItemType.MULTIPLE_CHOICE, form);

        form = new Form();
        form.param("meta", "3");
        form.param("statement", "<p>QUESTION 3</p>");
        form.param("score", "4");
        form.param("penalty", "-1");
        form.param("inputValidationRegex", "\\d+");
        form.param("gradingRegex", "123");
        String item4Jid = createBundleProblemItem(managerToken, problem3, ItemType.SHORT_ANSWER, form);

        form = new Form();
        form.param("meta", "4");
        form.param("statement", "<p>QUESTION 4</p>");
        form.param("score", "12");
        String item5Jid = createBundleProblemItem(managerToken, problem3, ItemType.ESSAY, form);

        problemClient.setProblems(managerToken, contest.getJid(), List.of(
                new ContestProblemData.Builder()
                        .alias("D")
                        .slug(PROBLEM_3_SLUG)
                        .status(OPEN)
                        .build()));

        judgels.uriel.api.contest.problem.bundle.ContestProblemWorksheet worksheet =
                problemClient.getBundleProblemWorksheet(
                        contestantToken,
                        contest.getJid(),
                        "D");

        assertThat(worksheet).isEqualTo(
                new judgels.uriel.api.contest.problem.bundle.ContestProblemWorksheet.Builder()
                        .defaultLanguage("en")
                        .languages(Set.of("en"))
                        .problem(new ContestProblem.Builder()
                                .alias("D")
                                .problemJid(problem3.getJid())
                                .status(OPEN)
                                .build())
                        .totalSubmissions(0)
                        .worksheet(new judgels.sandalphon.api.problem.bundle.ProblemWorksheet.Builder()
                                .statement(new ProblemStatement.Builder()
                                        .title("Problem 3")
                                        .text("Statement 3. <img src=\"http://localhost:8080/api/v2/problems/" + problem3.getJid() + "/render/image.png\"/>")
                                        .build())
                                .addItems(
                                        new Item.Builder()
                                                .jid(item1Jid)
                                                .type(ItemType.STATEMENT)
                                                .meta("1-2")
                                                .config(new StatementItemConfig.Builder()
                                                        .statement("<p>STATEMENT 1-2</p> <img src=\"http://localhost:8080/api/v2/problems/" + problem3.getJid() + "/render/statement.png\"/>")
                                                        .build())
                                                .build(),
                                        new Item.Builder()
                                                .jid(item2Jid)
                                                .type(ItemType.MULTIPLE_CHOICE)
                                                .number(1)
                                                .meta("1")
                                                .config(new MultipleChoiceItemConfig.Builder()
                                                        .statement("<p>QUESTION 1</p>")
                                                        .score(1)
                                                        .penalty(0)
                                                        .addChoices(
                                                                new MultipleChoiceItemConfig.Choice.Builder()
                                                                        .alias("a")
                                                                        .content("answer a <img src=\"http://localhost:8080/api/v2/problems/" + problem3.getJid() + "/render/a.png\"/>")
                                                                        .build(),
                                                                new MultipleChoiceItemConfig.Choice.Builder()
                                                                        .alias("b")
                                                                        .content("answer b")
                                                                        .build()
                                                        )
                                                        .build())
                                                .build(),
                                        new Item.Builder()
                                                .jid(item3Jid)
                                                .type(ItemType.MULTIPLE_CHOICE)
                                                .number(2)
                                                .meta("2")
                                                .config(new MultipleChoiceItemConfig.Builder()
                                                        .statement("<p>QUESTION 2</p>")
                                                        .score(4.0)
                                                        .penalty(-1.0)
                                                        .addChoices(
                                                                new MultipleChoiceItemConfig.Choice.Builder()
                                                                        .alias("a")
                                                                        .content("answer a")
                                                                        .build(),
                                                                new MultipleChoiceItemConfig.Choice.Builder()
                                                                        .alias("b")
                                                                        .content("answer b")
                                                                        .build()
                                                        )
                                                        .build())
                                                .build(),
                                        new Item.Builder()
                                                .jid(item4Jid)
                                                .type(ItemType.SHORT_ANSWER)
                                                .number(3)
                                                .meta("3")
                                                .config(new ShortAnswerItemConfig.Builder()
                                                        .statement("<p>QUESTION 3</p>")
                                                        .score(4.0)
                                                        .penalty(-1.0)
                                                        .inputValidationRegex("\\d+")
                                                        .build())
                                                .build(),
                                        new Item.Builder()
                                                .jid(item5Jid)
                                                .type(ItemType.ESSAY)
                                                .number(4)
                                                .meta("4")
                                                .config(new EssayItemConfig.Builder()
                                                        .statement("<p>QUESTION 4</p>")
                                                        .score(12.0)
                                                        .build())
                                                .build()
                                )
                                .reasonNotAllowedToSubmit(Optional.empty())
                                .build())
                        .build());
    }
}
