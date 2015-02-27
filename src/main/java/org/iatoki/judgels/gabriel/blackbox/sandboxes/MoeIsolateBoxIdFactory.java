package org.iatoki.judgels.gabriel.blackbox.sandboxes;

public final class MoeIsolateBoxIdFactory {
    private static int BOX_ID;

    private MoeIsolateBoxIdFactory() {
        // prevents instantiation
    }

    public static synchronized int newBoxId() {
        int currentBoxId = BOX_ID;
        BOX_ID = (BOX_ID + 1) % 100;

        return currentBoxId;
    }
}
