package org.iatoki.judgels.sandalphon.problem.bundle.item;

import com.google.common.collect.Lists;

public final class BundleItemUtils {

    private BundleItemUtils() {
        // prevent instantiation
    }

    public static BundleItemsConfig createDefaultItemConfig() {
        BundleItemsConfig itemConfig = new BundleItemsConfig();
        itemConfig.itemList = Lists.newArrayList();

        return itemConfig;
    }
}
