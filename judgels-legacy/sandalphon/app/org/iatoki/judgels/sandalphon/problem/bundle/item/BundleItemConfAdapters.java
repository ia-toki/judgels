package org.iatoki.judgels.sandalphon.problem.bundle.item;

public final class BundleItemConfAdapters {

    private BundleItemConfAdapters() {
        // prevent instantiation
    }

    public static BundleItemConfAdapter fromItemType(BundleItemType itemType) {
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
