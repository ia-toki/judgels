package org.iatoki.judgels.sandalphon.problem.bundle.item;

import play.twirl.api.Html;

public interface BundleItemAdapter {

    Html renderViewHtml(BundleItem bundleItem, String conf);
}
