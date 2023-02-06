package org.iatoki.judgels.sandalphon.problem.bundle.item.multiplechoice;

import com.fasterxml.jackson.databind.ObjectMapper;
import judgels.sandalphon.api.problem.bundle.BundleItem;
import judgels.sandalphon.api.problem.bundle.MultipleChoiceItemConfig;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemAdapter;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemHasScore;
import org.iatoki.judgels.sandalphon.problem.bundle.item.multiplechoice.html.multipleChoiceItemView;
import play.twirl.api.Html;

public final class MultipleChoiceItemAdapter implements BundleItemAdapter, BundleItemHasScore {
    private final MultipleChoiceItemConfigAdapter confAdapter;

    public MultipleChoiceItemAdapter(ObjectMapper mapper) {
        this.confAdapter = new MultipleChoiceItemConfigAdapter(mapper);
    }

    @Override
    public Html renderViewHtml(BundleItem item, String conf) {
        return multipleChoiceItemView.render(item, confAdapter.parseConfString(conf));
    }

    @Override
    public double calculateScore(String conf, String answer) {
        MultipleChoiceItemConfig config = confAdapter.parseConfString(conf);
        for (MultipleChoiceItemConfig.Choice itemChoice : config.getChoices()) {
            if (itemChoice.getAlias().equals(answer)) {
                if (itemChoice.getIsCorrect().orElse(false)) {
                    return config.getScore();
                } else {
                    return config.getPenalty();
                }
            }
        }
        return 0;
    }
}
