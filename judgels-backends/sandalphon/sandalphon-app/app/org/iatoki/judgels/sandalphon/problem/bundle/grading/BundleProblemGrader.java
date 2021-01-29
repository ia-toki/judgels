package org.iatoki.judgels.sandalphon.problem.bundle.grading;

import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import judgels.sandalphon.api.problem.bundle.BundleItem;
import org.iatoki.judgels.sandalphon.problem.base.ProblemStore;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemAdapter;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemAdapters;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemConfAdapter;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemConfAdapters;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemHasScore;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemStore;

public final class BundleProblemGrader {

    private final BundleItemStore bundleItemStore;
    private final ProblemStore problemStore;

    @Inject
    public BundleProblemGrader(BundleItemStore bundleItemStore, ProblemStore problemStore) {
        this.bundleItemStore = bundleItemStore;
        this.problemStore = problemStore;
    }

    public BundleGradingResult gradeBundleProblem(String problemJid, BundleAnswer answer) {
        List<BundleItem> items = bundleItemStore.getBundleItemsInProblemWithClone(problemJid, null);
        ImmutableMap.Builder<String, BundleDetailResult> detailResultBuilder = ImmutableMap.builder();

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
                                    .getDefaultLanguage(null, problemJid));
                } else {
                    throw e;
                }
            }

            BundleItemConfAdapter confAdapter = BundleItemConfAdapters.fromItemType(item.getType());
            BundleItemAdapter adapter = BundleItemAdapters.fromItemType(item.getType());
            if ((adapter instanceof BundleItemHasScore) && answer.getAnswers().containsKey(item.getJid())) {
                double score = ((BundleItemHasScore) adapter).calculateScore(confAdapter.parseConfString(conf), answer.getAnswers().get(item.getJid()));
                detailResultBuilder.put(item.getJid(), new BundleDetailResult(item.getNumber().orElse(0), score));
                totalScore += score;
            }
        }

        return new BundleGradingResult(totalScore, detailResultBuilder.build());
    }
}
