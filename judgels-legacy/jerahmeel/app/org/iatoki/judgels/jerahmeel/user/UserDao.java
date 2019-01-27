package org.iatoki.judgels.jerahmeel.user;

import com.google.inject.ImplementedBy;
import judgels.persistence.Dao;

@ImplementedBy(UserHibernateDao.class)
public interface UserDao extends Dao<UserModel> {

    boolean existsByJid(String jid);

    UserModel findByJid(String jid);
}
