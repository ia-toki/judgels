package org.iatoki.judgels.sandalphon.problem.bundle.item;

import judgels.sandalphon.api.problem.bundle.ItemType;

public final class BundleItemConfAdapters {

    private BundleItemConfAdapters() {
        // prevent instantiation
    }

    public static BundleItemConfAdapter fromItemType(ItemType itemType) {
        BundleItemConfAdapter itemConfAdapter = null;

        switch (itemType) {
            case STATEMENT:
                itemConfAdapter = new ItemStatementConfAdapter();
                break;
            case MULTIPLE_CHOICE:
                itemConfAdapter = new ItemMultipleChoiceConfAdapter();
                break;
            case SHORT_ANSWER:
                itemConfAdapter = new ItemShortAnswerConfAdapter();
                break;
            case ESSAY:
                itemConfAdapter = new ItemEssayConfAdapter();
                break;
            default: break;
        }

        return itemConfAdapter;
    }
}
