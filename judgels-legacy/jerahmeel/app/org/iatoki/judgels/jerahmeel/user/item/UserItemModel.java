package org.iatoki.judgels.jerahmeel.user.item;

import judgels.persistence.Model;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity(name = "jerahmeel_user_item")
@Table(indexes = {@Index(columnList = "userJid,itemJid", unique = true)})
public final class UserItemModel extends Model {
    public String userJid;

    public String itemJid;

    public String status;
}
