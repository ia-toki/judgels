package org.iatoki.judgels.sandalphon.problem.bundle.item;

import com.google.gson.Gson;
import org.iatoki.judgels.sandalphon.problem.bundle.item.html.itemShortAnswerView;
import play.twirl.api.Html;

public final class ItemShortAnswerAdapter implements BundleItemAdapter, BundleItemHasScore {
    @Override
    public Html renderViewHtml(BundleItem bundleItem, String conf) {
        return itemShortAnswerView.render(bundleItem, new Gson().fromJson(conf, ItemShortAnswerConf.class));
    }

    @Override
    public double calculateScore(BundleItemConf conf, String answer) {
        ItemShortAnswerConf realConf = (ItemShortAnswerConf) conf;
        if (answer.matches(realConf.gradingRegex)) {
            return realConf.score;
        } else {
            return realConf.penalty;
        }
    }
}
