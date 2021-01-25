package org.iatoki.judgels.sandalphon.problem.bundle.grading;

import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import org.iatoki.judgels.sandalphon.problem.base.ProblemStore;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItem;
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
        List<BundleItem> bundleItems = bundleItemStore.getBundleItemsInProblemWithClone(problemJid, null);
        ImmutableMap.Builder<String, BundleDetailResult> detailResultBuilder = ImmutableMap.builder();

        double totalScore = 0;
        for (BundleItem bundleItem : bundleItems) {
            String conf = "";
            try {
                conf = bundleItemStore.getItemConfInProblemWithCloneByJid(problemJid, null, bundleItem.getJid(), answer.getLanguageCode());
            } catch (RuntimeException e) {
                if (e.getCause() instanceof IOException) {
                    conf = bundleItemStore.getItemConfInProblemWithCloneByJid(problemJid,
                            null,
                            bundleItem.getJid(),
                            problemStore
                                    .getDefaultLanguage(null, problemJid));
                }
            }

            BundleItemConfAdapter confAdapter = BundleItemConfAdapters.fromItemType(bundleItem.getType());
            BundleItemAdapter adapter = BundleItemAdapters.fromItemType(bundleItem.getType());
            if ((adapter instanceof BundleItemHasScore) && answer.getAnswers().containsKey(bundleItem.getJid())) {
                double score = ((BundleItemHasScore) adapter).calculateScore(confAdapter.parseConfString(conf), answer.getAnswers().get(bundleItem.getJid()));
                detailResultBuilder.put(bundleItem.getJid(), new BundleDetailResult(bundleItem.getNumber(), score));
                totalScore += score;
            }
        }

        return new BundleGradingResult(totalScore, detailResultBuilder.build());
    }
}
