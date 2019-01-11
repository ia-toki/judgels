package org.iatoki.judgels.jerahmeel.user.item;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.model.Dao;

import java.util.List;

@ImplementedBy(UserItemHibernateDao.class)
public interface UserItemDao extends Dao<Long, UserItemModel> {

    boolean existsByUserJidAndItemJid(String userJid, String itemJid);

    boolean existsByUserJidItemJidAndStatus(String userJid, String itemJid, String status);

    UserItemModel findByUserJidAndItemJid(String userJid, String itemJid);

    List<UserItemModel> getByUserJid(String userJid);

    List<UserItemModel> getByItemJid(String itemJid);

    List<UserItemModel> getByUserJidAndStatus(String userJid, String status);
}
