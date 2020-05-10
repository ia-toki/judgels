package org.iatoki.judgels.sandalphon.problem.bundle.item;

import com.google.gson.Gson;
import org.iatoki.judgels.sandalphon.problem.bundle.item.html.itemEssayView;
import play.twirl.api.Html;

public final class ItemEssayAdapter implements BundleItemAdapter, BundleItemHasScore {
    @Override
    public Html renderViewHtml(BundleItem bundleItem, String conf) {
        return itemEssayView.render(bundleItem, new Gson().fromJson(conf, ItemEssayConf.class));
    }

    @Override
    public double calculateScore(BundleItemConf conf, String answer) {
        return 0; // Essay items are to be graded manually
    }
}
