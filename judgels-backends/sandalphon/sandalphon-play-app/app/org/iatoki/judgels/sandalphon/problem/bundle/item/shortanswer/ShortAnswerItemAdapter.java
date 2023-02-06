package org.iatoki.judgels.sandalphon.problem.bundle.item.shortanswer;

import com.fasterxml.jackson.databind.ObjectMapper;
import judgels.sandalphon.api.problem.bundle.BundleItem;
import judgels.sandalphon.api.problem.bundle.ShortAnswerItemConfig;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemAdapter;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemHasScore;
import org.iatoki.judgels.sandalphon.problem.bundle.item.shortanswer.html.shortAnswerItemView;
import play.twirl.api.Html;

public final class ShortAnswerItemAdapter implements BundleItemAdapter, BundleItemHasScore {
    private final ShortAnswerItemConfigAdapter confAdapter;

    public ShortAnswerItemAdapter(ObjectMapper mapper) {
        this.confAdapter = new ShortAnswerItemConfigAdapter(mapper);
    }

    @Override
    public Html renderViewHtml(BundleItem item, String conf) {
        return shortAnswerItemView.render(item, confAdapter.parseConfString(conf));
    }

    @Override
    public double calculateScore(String conf, String answer) {
        ShortAnswerItemConfig config = confAdapter.parseConfString(conf);
        if (answer.matches(config.getGradingRegex().orElse(""))) {
            return config.getScore();
        } else {
            return config.getPenalty();
        }
    }
}
