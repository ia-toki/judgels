package org.iatoki.judgels.sandalphon.problem.bundle.item;

import com.google.gson.Gson;
import judgels.sandalphon.api.problem.bundle.BundleItem;
import org.iatoki.judgels.sandalphon.problem.bundle.item.html.itemShortAnswerView;
import play.twirl.api.Html;

public final class ItemShortAnswerAdapter implements BundleItemAdapter, BundleItemHasScore {
    @Override
    public Html renderViewHtml(BundleItem item, String conf) {
        return itemShortAnswerView.render(item, new Gson().fromJson(conf, ItemShortAnswerConf.class));
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
