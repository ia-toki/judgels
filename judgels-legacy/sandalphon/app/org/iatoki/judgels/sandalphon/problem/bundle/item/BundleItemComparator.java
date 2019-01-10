package org.iatoki.judgels.sandalphon.problem.bundle.item;

import java.util.Comparator;

public final class BundleItemComparator implements Comparator<BundleItem> {

    private final String orderBy;
    private final String orderDir;

    public BundleItemComparator(String orderBy, String orderDir) {
        this.orderBy = orderBy;
        this.orderDir = orderDir;
    }

    @Override
    public int compare(BundleItem o1, BundleItem o2) {
        BundleItem usedO1 = o1;
        BundleItem usedO2 = o2;
        if (orderDir.equals("asc")) {
            BundleItem temp = usedO2;
            usedO2 = usedO1;
            usedO1 = temp;
        }
        switch (orderBy) {
            case "jid":
                return usedO1.getJid().compareTo(usedO2.getJid());
            case "type":
                return usedO1.getType().compareTo(usedO2.getType());
            case "meta":
                return usedO1.getMeta().compareTo(usedO2.getMeta());
            default: break;
        }
        return 0;
    }
}
