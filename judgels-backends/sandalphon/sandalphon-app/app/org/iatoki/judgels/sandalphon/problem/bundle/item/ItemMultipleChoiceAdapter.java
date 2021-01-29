package org.iatoki.judgels.sandalphon.problem.bundle.item;

import com.google.gson.Gson;
import judgels.sandalphon.api.problem.bundle.BundleItem;
import org.iatoki.judgels.sandalphon.problem.bundle.item.html.itemMultipleChoiceView;
import play.twirl.api.Html;

public final class ItemMultipleChoiceAdapter implements BundleItemAdapter, BundleItemHasScore {

    @Override
    public Html renderViewHtml(BundleItem item, String conf) {
        return itemMultipleChoiceView.render(item, new Gson().fromJson(conf, ItemMultipleChoiceConf.class));
    }

    @Override
    public double calculateScore(BundleItemConf conf, String answer) {
        ItemMultipleChoiceConf realConf = (ItemMultipleChoiceConf) conf;
        for (ItemChoice itemChoice : realConf.choices) {
            if (itemChoice.getAlias().equals(answer)) {
                if (itemChoice.isCorrect()) {
                    return realConf.score;
                } else {
                    return realConf.penalty;
                }
            }
        }
        return 0;
    }
}

