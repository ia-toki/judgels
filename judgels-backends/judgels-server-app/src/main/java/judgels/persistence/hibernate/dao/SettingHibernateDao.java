package judgels.persistence.hibernate.dao;

import jakarta.inject.Inject;
import java.util.Optional;
import judgels.persistence.dao.SettingDao;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.model.SettingModel;
import judgels.persistence.model.SettingModel_;

public class SettingHibernateDao extends HibernateDao<SettingModel> implements SettingDao {
    @Inject
    public SettingHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<SettingModel> selectByKey(String key) {
        return select().where(columnEq(SettingModel_.settingKey, key)).unique();
    }
}
