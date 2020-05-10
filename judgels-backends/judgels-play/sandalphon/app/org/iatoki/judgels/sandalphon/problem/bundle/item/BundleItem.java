package org.iatoki.judgels.sandalphon.problem.bundle.item;

public final class BundleItem {

    private transient Long number;
    private final String jid;
    private final BundleItemType type;
    private final String meta;

    public BundleItem(String jid, BundleItemType type, String meta) {
        this.jid = jid;
        this.type = type;
        this.meta = meta;
    }

    public Long getNumber() {
        return number;
    }

    public String getJid() {
        return jid;
    }

    public BundleItemType getType() {
        return type;
    }

    public String getMeta() {
        return meta;
    }

    public void setNumber(Long number) {
        this.number = number;
    }
}
