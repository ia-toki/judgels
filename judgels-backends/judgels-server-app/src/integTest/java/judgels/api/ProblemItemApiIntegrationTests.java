package judgels.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import judgels.BaseJudgelsApiIntegrationTests;
import judgels.api.catalog.problem.Problem;
import judgels.api.catalog.problem.bundle.EssayItemConfig;
import judgels.api.catalog.problem.bundle.Item;
import judgels.api.catalog.problem.bundle.ItemConfig;
import judgels.api.catalog.problem.bundle.ItemType;
import judgels.api.catalog.problem.bundle.MultipleChoiceItemConfig;
import judgels.api.catalog.problem.bundle.ShortAnswerItemConfig;
import judgels.api.catalog.problem.bundle.StatementItemConfig;
import judgels.api.catalog.problem.bundle.item.ProblemItemCreateData;
import judgels.api.catalog.problem.bundle.item.ProblemItemUpdateData;
import judgels.client.ProblemClient;
import judgels.client.ProblemItemClient;
import judgels.client.ProblemStatementClient;
import org.junit.jupiter.api.Test;

class ProblemItemApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final ProblemClient problemClient = createClient(ProblemClient.class);
    private final ProblemItemClient itemClient = createClient(ProblemItemClient.class);
    private final ProblemStatementClient statementClient = createClient(ProblemStatementClient.class);

    @Test
    void create_update_items() {
        Problem problem = createBundleProblem(adminToken, "items-problem");
        String problemJid = problem.getJid();

        assertThat(itemClient.getItems(adminToken, problemJid, null).getData()).isEmpty();
        assertThat(problemClient.getProblem(adminToken, problemJid).getHasLocalChanges()).isFalse();

        Item statement = itemClient.createItem(adminToken, problemJid, type(ItemType.STATEMENT));
        Item multipleChoice = itemClient.createItem(adminToken, problemJid, type(ItemType.MULTIPLE_CHOICE));
        Item shortAnswer = itemClient.createItem(adminToken, problemJid, type(ItemType.SHORT_ANSWER));
        Item essay = itemClient.createItem(adminToken, problemJid, type(ItemType.ESSAY));

        // a statement item is not numbered
        assertThat(statement.getNumber()).isEmpty();
        assertThat(statement.getMeta()).isEmpty();
        assertThat(statement.getConfig()).isEqualTo(new StatementItemConfig.Builder().statement("").build());
        assertThat(multipleChoice.getNumber()).contains(1);
        assertThat(((MultipleChoiceItemConfig) multipleChoice.getConfig()).getChoices())
                .extracting(MultipleChoiceItemConfig.Choice::getAlias)
                .containsExactly("a", "b", "c", "d", "e");
        assertThat(shortAnswer.getNumber()).contains(2);
        assertThat(essay.getNumber()).contains(3);

        assertThat(itemClient.getItems(adminToken, problemJid, null).getData())
                .containsExactly(statement, multipleChoice, shortAnswer, essay);
        assertThat(problemClient.getProblem(adminToken, problemJid).getHasLocalChanges()).isTrue();

        MultipleChoiceItemConfig multipleChoiceConfig = new MultipleChoiceItemConfig.Builder()
                .statement("<p>What is 1 + 1?</p>")
                .score(4)
                .penalty(-1)
                .addChoices(
                        new MultipleChoiceItemConfig.Choice.Builder().alias("a").content("1").isCorrect(false).build(),
                        new MultipleChoiceItemConfig.Choice.Builder().alias("b").content("2").isCorrect(true).build())
                .build();
        itemClient.updateItem(
                adminToken,
                problemJid,
                multipleChoice.getJid(),
                "en-US",
                data(ItemType.MULTIPLE_CHOICE, "addition", multipleChoiceConfig));

        ShortAnswerItemConfig shortAnswerConfig = new ShortAnswerItemConfig.Builder()
                .statement("<p>What is 2 + 2?</p>")
                .score(2)
                .penalty(0)
                .inputValidationRegex("\\d+")
                .gradingRegex("4")
                .build();
        itemClient.updateItem(
                adminToken,
                problemJid,
                shortAnswer.getJid(),
                "en-US",
                data(ItemType.SHORT_ANSWER, "", shortAnswerConfig));

        EssayItemConfig essayConfig = new EssayItemConfig.Builder().statement("<p>Explain.</p>").score(10).build();
        itemClient.updateItem(adminToken, problemJid, essay.getJid(), "en-US", data(ItemType.ESSAY, "", essayConfig));

        assertThat(itemClient.getItem(adminToken, problemJid, multipleChoice.getJid(), null))
                .isEqualTo(new Item.Builder()
                        .from(multipleChoice)
                        .meta("addition")
                        .config(multipleChoiceConfig)
                        .build());
        assertThat(itemClient.getItems(adminToken, problemJid, "en-US").getData())
                .extracting(Item::getConfig)
                .containsExactly(statement.getConfig(), multipleChoiceConfig, shortAnswerConfig, essayConfig);

        // a config of another type
        assertBadRequest(() -> itemClient.updateItem(
                adminToken,
                problemJid,
                multipleChoice.getJid(),
                "en-US",
                data(ItemType.ESSAY, "", essayConfig)));

        // choices that share an alias
        assertBadRequest(() -> itemClient.updateItem(
                adminToken,
                problemJid,
                multipleChoice.getJid(),
                "en-US",
                data(ItemType.MULTIPLE_CHOICE, "", new MultipleChoiceItemConfig.Builder()
                        .from(multipleChoiceConfig)
                        .addChoices(new MultipleChoiceItemConfig.Choice.Builder().alias("a").content("3").build())
                        .build())));

        // a regex that does not compile
        assertBadRequest(() -> itemClient.updateItem(
                adminToken,
                problemJid,
                shortAnswer.getJid(),
                "en-US",
                data(ItemType.SHORT_ANSWER, "", new ShortAnswerItemConfig.Builder()
                        .from(shortAnswerConfig)
                        .gradingRegex(Optional.of("("))
                        .build())));

        assertBadRequest(() -> itemClient.getItems(adminToken, problemJid, "id-ID"));
        assertBadRequest(() -> itemClient.getItem(adminToken, problemJid, essay.getJid(), "id-ID"));
        assertBadRequest(() -> itemClient.updateItem(
                adminToken,
                problemJid,
                essay.getJid(),
                "id-ID",
                data(ItemType.ESSAY, "", essayConfig)));

        assertNotFound(() -> itemClient.getItem(adminToken, problemJid, "JIDITEMbogus", null));
        assertNotFound(() -> itemClient.updateItem(
                adminToken,
                problemJid,
                "JIDITEMbogus",
                "en-US",
                data(ItemType.ESSAY, "", essayConfig)));

        assertForbidden(() -> itemClient.getItems(userToken, problemJid, null));
        assertForbidden(() -> itemClient.getItem(userToken, problemJid, essay.getJid(), null));
        assertForbidden(() -> itemClient.createItem(userToken, problemJid, type(ItemType.ESSAY)));
        assertForbidden(() -> itemClient.updateItem(
                userToken,
                problemJid,
                essay.getJid(),
                "en-US",
                data(ItemType.ESSAY, "", essayConfig)));

        Problem programmingProblem = createProblem(adminToken, "items-programming-problem");
        assertNotFound(() -> itemClient.getItems(adminToken, programmingProblem.getJid(), null));
        assertNotFound(() -> itemClient.createItem(adminToken, programmingProblem.getJid(), type(ItemType.ESSAY)));
    }

    @Test
    void update_item_in_another_language() {
        Problem problem = createBundleProblem(adminToken, "items-languages-problem");
        String problemJid = problem.getJid();

        Item essay = itemClient.createItem(adminToken, problemJid, type(ItemType.ESSAY));
        EssayItemConfig config = new EssayItemConfig.Builder().statement("<p>Explain.</p>").score(10).build();
        itemClient.updateItem(adminToken, problemJid, essay.getJid(), "en-US", data(ItemType.ESSAY, "essay", config));

        // a language without its own config falls back to the default language's
        statementClient.addStatementLanguage(adminToken, problemJid, "id-ID");
        assertThat(itemClient.getItem(adminToken, problemJid, essay.getJid(), "id-ID").getConfig()).isEqualTo(config);

        EssayItemConfig idConfig = new EssayItemConfig.Builder().statement("<p>Jelaskan.</p>").score(10).build();
        itemClient.updateItem(adminToken, problemJid, essay.getJid(), "id-ID", data(ItemType.ESSAY, "essay", idConfig));

        assertThat(itemClient.getItem(adminToken, problemJid, essay.getJid(), "id-ID").getConfig()).isEqualTo(idConfig);
        assertThat(itemClient.getItem(adminToken, problemJid, essay.getJid(), "en-US").getConfig()).isEqualTo(config);
        assertThat(itemClient.getItems(adminToken, problemJid, "id-ID").getData())
                .extracting(Item::getConfig)
                .containsExactly(idConfig);
        assertThat(itemClient.getItems(adminToken, problemJid, null).getData())
                .extracting(Item::getConfig)
                .containsExactly(config);
    }

    @Test
    void move_delete_items() {
        Problem problem = createBundleProblem(adminToken, "items-order-problem");
        String problemJid = problem.getJid();

        String item1Jid = itemClient.createItem(adminToken, problemJid, type(ItemType.ESSAY)).getJid();
        String item2Jid = itemClient.createItem(adminToken, problemJid, type(ItemType.STATEMENT)).getJid();
        String item3Jid = itemClient.createItem(adminToken, problemJid, type(ItemType.SHORT_ANSWER)).getJid();

        itemClient.moveItemUp(adminToken, problemJid, item3Jid);
        assertThat(itemClient.getItems(adminToken, problemJid, null).getData())
                .extracting(Item::getJid)
                .containsExactly(item1Jid, item3Jid, item2Jid);

        itemClient.moveItemDown(adminToken, problemJid, item1Jid);
        assertThat(itemClient.getItems(adminToken, problemJid, null).getData())
                .extracting(Item::getJid)
                .containsExactly(item3Jid, item1Jid, item2Jid);

        // the items are renumbered in their new order
        assertThat(itemClient.getItem(adminToken, problemJid, item3Jid, null).getNumber()).contains(1);
        assertThat(itemClient.getItem(adminToken, problemJid, item1Jid, null).getNumber()).contains(2);

        // the first item stays first, and the last item stays last
        itemClient.moveItemUp(adminToken, problemJid, item3Jid);
        itemClient.moveItemDown(adminToken, problemJid, item2Jid);
        assertThat(itemClient.getItems(adminToken, problemJid, null).getData())
                .extracting(Item::getJid)
                .containsExactly(item3Jid, item1Jid, item2Jid);

        itemClient.deleteItem(adminToken, problemJid, item3Jid);
        assertThat(itemClient.getItems(adminToken, problemJid, null).getData())
                .extracting(Item::getJid)
                .containsExactly(item1Jid, item2Jid);
        assertThat(itemClient.getItem(adminToken, problemJid, item1Jid, null).getNumber()).contains(1);

        assertNotFound(() -> itemClient.moveItemUp(adminToken, problemJid, item3Jid));
        assertNotFound(() -> itemClient.moveItemDown(adminToken, problemJid, item3Jid));
        assertNotFound(() -> itemClient.deleteItem(adminToken, problemJid, item3Jid));

        assertForbidden(() -> itemClient.moveItemUp(userToken, problemJid, item2Jid));
        assertForbidden(() -> itemClient.moveItemDown(userToken, problemJid, item1Jid));
        assertForbidden(() -> itemClient.deleteItem(userToken, problemJid, item1Jid));
    }

    private static ProblemItemCreateData type(ItemType type) {
        return new ProblemItemCreateData.Builder().type(type).build();
    }

    private static ProblemItemUpdateData data(ItemType type, String meta, ItemConfig config) {
        return new ProblemItemUpdateData.Builder().type(type).meta(meta).config(config).build();
    }
}
