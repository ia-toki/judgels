package org.iatoki.judgels.sandalphon.problem.bundle.grading;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import judgels.sandalphon.api.problem.bundle.BundleItem;
import judgels.sandalphon.api.submission.bundle.BundleAnswer;
import judgels.sandalphon.api.submission.bundle.BundleGradingResult;
import judgels.sandalphon.api.submission.bundle.ItemGradingResult;
import judgels.sandalphon.problem.base.ProblemStore;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemAdapter;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemAdapters;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemHasScore;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemStore;

public final class BundleProblemGrader {
    private final ObjectMapper mapper;
    private final BundleItemStore bundleItemStore;
    private final ProblemStore problemStore;

    @Inject
    public BundleProblemGrader(ObjectMapper mapper, BundleItemStore bundleItemStore, ProblemStore problemStore) {
        this.mapper = mapper;
        this.bundleItemStore = bundleItemStore;
        this.problemStore = problemStore;
    }

    public BundleGradingResult gradeBundleProblem(String problemJid, BundleAnswer answer) {
        List<BundleItem> items = bundleItemStore.getBundleItemsInProblemWithClone(problemJid, null);
        ImmutableMap.Builder<String, ItemGradingResult> details = ImmutableMap.builder();

        double totalScore = 0;
        for (BundleItem item : items) {
            String conf;
            try {
                conf = bundleItemStore.getItemConfInProblemWithCloneByJid(problemJid, null, item.getJid(), answer.getLanguageCode());
            } catch (RuntimeException e) {
                if (e.getCause() instanceof IOException) {
                    conf = bundleItemStore.getItemConfInProblemWithCloneByJid(problemJid,
                            null,
                            item.getJid(),
                            problemStore
                                    .getStatementDefaultLanguage(null, problemJid));
                } else {
                    throw e;
                }
            }

            BundleItemAdapter adapter = BundleItemAdapters.fromItemType(item.getType(), mapper);
            if (adapter instanceof BundleItemHasScore && answer.getAnswers().containsKey(item.getJid())) {
                double score = ((BundleItemHasScore) adapter).calculateScore(conf, answer.getAnswers().get(item.getJid()));
                details.put(item.getJid(), new ItemGradingResult.Builder()
                        .number(item.getNumber().orElse(0))
                        .score(score)
                        .build());
                totalScore += score;
            }
        }

        return new BundleGradingResult.Builder()
                .score(totalScore)
                .details(details.build())
                .build();
    }
}
