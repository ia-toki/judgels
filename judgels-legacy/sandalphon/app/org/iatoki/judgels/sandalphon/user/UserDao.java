package org.iatoki.judgels.sandalphon.user;

import com.google.inject.ImplementedBy;
import judgels.persistence.Dao;

@ImplementedBy(UserHibernateDao.class)
public interface UserDao extends Dao<UserModel> {

    boolean existsByJid(String userJid);

    UserModel findByJid(String userJid);
}
