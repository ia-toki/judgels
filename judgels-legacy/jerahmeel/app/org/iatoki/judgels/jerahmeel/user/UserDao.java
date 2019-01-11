package org.iatoki.judgels.jerahmeel.user;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.model.Dao;

@ImplementedBy(UserHibernateDao.class)
public interface UserDao extends Dao<Long, UserModel> {

    boolean existsByJid(String jid);

    UserModel findByJid(String jid);
}
