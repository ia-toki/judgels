package org.iatoki.judgels.sandalphon.problem.bundle;

import com.google.common.collect.ImmutableMap;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleAnswer;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleDetailResult;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleGradingResult;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItem;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemAdapter;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemAdapters;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemConfAdapter;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemConfAdapters;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemHasScore;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemService;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleProblemGrader;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;

@Singleton
public final class BundleProblemGraderImpl implements BundleProblemGrader {

    private final BundleItemService bundleItemService;
    private final ProblemService problemService;

    @Inject
    public BundleProblemGraderImpl(BundleItemService bundleItemService, ProblemService problemService) {
        this.bundleItemService = bundleItemService;
        this.problemService = problemService;
    }

    @Override
    public BundleGradingResult gradeBundleProblem(String problemJid, BundleAnswer answer) throws IOException {
        List<BundleItem> bundleItems = bundleItemService.getBundleItemsInProblemWithClone(problemJid, null);
        ImmutableMap.Builder<String, BundleDetailResult> detailResultBuilder = ImmutableMap.builder();

        double totalScore = 0;
        for (BundleItem bundleItem : bundleItems) {
            String conf = "";
            try {
                conf = bundleItemService.getItemConfInProblemWithCloneByJid(problemJid, null, bundleItem.getJid(), answer.getLanguageCode());
            } catch (IOException e) {
                conf = bundleItemService.getItemConfInProblemWithCloneByJid(problemJid, null, bundleItem.getJid(), problemService.getDefaultLanguage(null, problemJid));
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
