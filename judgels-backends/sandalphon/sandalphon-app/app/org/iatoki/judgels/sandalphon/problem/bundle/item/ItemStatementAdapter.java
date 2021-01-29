package org.iatoki.judgels.sandalphon.problem.bundle.item;

import com.google.gson.Gson;
import judgels.sandalphon.api.problem.bundle.BundleItem;
import org.iatoki.judgels.sandalphon.problem.bundle.item.html.itemStatementView;
import play.twirl.api.Html;

public final class ItemStatementAdapter implements BundleItemAdapter {

    @Override
    public Html renderViewHtml(BundleItem item, String conf) {
        return itemStatementView.render(item, new Gson().fromJson(conf, ItemStatementConf.class));
    }
}
