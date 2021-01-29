package org.iatoki.judgels.sandalphon.problem.bundle.item;

import judgels.sandalphon.api.problem.bundle.BundleItem;
import play.twirl.api.Html;

public interface BundleItemAdapter {

    Html renderViewHtml(BundleItem item, String conf);
}
