package org.iatoki.judgels.jerahmeel.user.item;

public final class UserItem {

    private final String userJid;
    private final String itemJid;
    private final UserItemStatus status;

    public UserItem(String userJid, String itemJid, UserItemStatus status) {
        this.userJid = userJid;
        this.itemJid = itemJid;
        this.status = status;
    }

    public String getUserJid() {
        return userJid;
    }

    public String getItemJid() {
        return itemJid;
    }

    public UserItemStatus getStatus() {
        return status;
    }
}
