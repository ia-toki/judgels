package org.iatoki.judgels.sandalphon.problem.bundle.item;

public final class BundleItemAdapters {

    private BundleItemAdapters() {
        // prevent instantiation
    }

    public static BundleItemAdapter fromItemType(BundleItemType itemType) {
        BundleItemAdapter itemAdapter = null;

        switch (itemType) {
            case STATEMENT:
                itemAdapter = new ItemStatementAdapter();
                break;
            case MULTIPLE_CHOICE:
                itemAdapter = new ItemMultipleChoiceAdapter();
                break;
            default: break;
        }

        return itemAdapter;
    }
}
