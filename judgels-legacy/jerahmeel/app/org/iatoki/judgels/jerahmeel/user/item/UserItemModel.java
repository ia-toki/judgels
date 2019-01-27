package org.iatoki.judgels.jerahmeel.user.item;

import judgels.persistence.Model;

import javax.persistence.Entity;

@Entity(name = "jerahmeel_user_item")
public final class UserItemModel extends Model {
    public String userJid;

    public String itemJid;

    public String status;
}
