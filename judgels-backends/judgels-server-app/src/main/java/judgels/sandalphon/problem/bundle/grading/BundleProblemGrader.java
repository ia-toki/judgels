package judgels.sandalphon.problem.bundle.grading;

import jakarta.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import judgels.sandalphon.api.problem.bundle.BundleItem;
import judgels.sandalphon.api.problem.bundle.ItemConfig;
import judgels.sandalphon.api.problem.bundle.ItemType;
import judgels.sandalphon.api.submission.bundle.BundleAnswer;
import judgels.sandalphon.api.submission.bundle.BundleGradingResult;
import judgels.sandalphon.api.submission.bundle.ItemGradingResult;
import judgels.sandalphon.problem.base.statement.ProblemStatementStore;
import judgels.sandalphon.problem.bundle.item.BundleItemStore;
import judgels.sandalphon.problem.bundle.item.ItemEngine;
import judgels.sandalphon.problem.bundle.item.ItemEngineRegistry;

public final class BundleProblemGrader {
    private final BundleItemStore itemStore;
    private final ProblemStatementStore statementStore;

    @Inject
    public BundleProblemGrader(BundleItemStore itemStore, ProblemStatementStore statementStore) {
        this.itemStore = itemStore;
        this.statementStore = statementStore;
    }

    public BundleGradingResult grade(String problemJid, BundleAnswer answer) {
        List<BundleItem> items = itemStore.getNumberedItems(null, problemJid);
        Map<String, ItemGradingResult> details = new HashMap<>();

        String defaultLanguage = statementStore.getStatementDefaultLanguage(null, problemJid);

        double totalScore = 0;
        for (BundleItem item : items) {
            if (item.getType() != ItemType.STATEMENT && answer.getAnswers().containsKey(item.getJid())) {
                ItemConfig config = itemStore.getItemConfig(null, problemJid, item, answer.getLanguageCode(), defaultLanguage);
                ItemEngine engine = ItemEngineRegistry.getByType(item.getType());

                double score = engine.calculateScore(config, answer.getAnswers().get(item.getJid()));
                details.put(item.getJid(), new ItemGradingResult.Builder()
                        .number(item.getNumber().orElse(0))
                        .score(score)
                        .build());
                totalScore += score;
            }
        }

        return new BundleGradingResult.Builder()
                .score(totalScore)
                .details(details)
                .build();
    }
}
