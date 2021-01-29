package org.iatoki.judgels.sandalphon.problem.bundle.item;

import judgels.sandalphon.api.problem.bundle.ItemType;

public final class BundleItemAdapters {
    private BundleItemAdapters() {}

    public static BundleItemAdapter fromItemType(ItemType itemType) {
        BundleItemAdapter itemAdapter = null;

        switch (itemType) {
            case STATEMENT:
                itemAdapter = new ItemStatementAdapter();
                break;
            case MULTIPLE_CHOICE:
                itemAdapter = new ItemMultipleChoiceAdapter();
                break;
            case SHORT_ANSWER:
                itemAdapter = new ItemShortAnswerAdapter();
                break;
            case ESSAY:
                itemAdapter = new ItemEssayAdapter();
                break;
            default: break;
        }

        return itemAdapter;
    }
}
