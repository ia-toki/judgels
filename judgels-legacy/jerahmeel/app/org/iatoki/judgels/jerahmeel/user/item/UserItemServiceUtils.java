package org.iatoki.judgels.jerahmeel.user.item;

final class UserItemServiceUtils {

    private UserItemServiceUtils() {
        // prevent instantiation
    }

    static UserItem createFromModel(UserItemModel u) {
        return new UserItem(u.userJid, u.itemJid, UserItemStatus.valueOf(u.status));
    }
}
