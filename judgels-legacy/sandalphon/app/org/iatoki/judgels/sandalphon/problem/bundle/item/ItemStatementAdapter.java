package org.iatoki.judgels.sandalphon.problem.bundle.item;

import com.google.gson.Gson;
import org.iatoki.judgels.sandalphon.problem.bundle.item.html.itemStatementView;
import play.twirl.api.Html;

public final class ItemStatementAdapter implements BundleItemAdapter {

    @Override
    public Html renderViewHtml(BundleItem bundleItem, String conf) {
        return itemStatementView.render(bundleItem, new Gson().fromJson(conf, ItemStatementConf.class));
    }
}
