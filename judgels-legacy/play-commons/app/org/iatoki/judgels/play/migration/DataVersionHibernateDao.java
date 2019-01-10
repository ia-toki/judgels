package org.iatoki.judgels.play.migration;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public final class DataVersionHibernateDao implements DataVersionDao {

    private final EntityManager entityManager;

    public DataVersionHibernateDao() {
        this.entityManager = DataMigrationEntityManager.createEntityManager();
    }

    @Override
    public long getVersion() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DataVersionModel> query = cb.createQuery(DataVersionModel.class);
        query.from(DataVersionModel.class);

        List<DataVersionModel> dataVersionModels = entityManager.createQuery(query).getResultList();

        if (dataVersionModels.isEmpty()) {
            return 0;
        }

        return dataVersionModels.get(0).version;
    }

    @Override
    public void update(long version) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DataVersionModel> query = cb.createQuery(DataVersionModel.class);
        query.from(DataVersionModel.class);

        List<DataVersionModel> dataVersionModels = entityManager.createQuery(query).getResultList();

        if (dataVersionModels.isEmpty()) {
            DataVersionModel dataVersionModel = new DataVersionModel();
            dataVersionModel.version = version;

            entityManager.persist(dataVersionModel);
        } else {
            DataVersionModel dataVersionModel =  entityManager.createQuery(query).getSingleResult();
            dataVersionModel.version = version;

            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            entityManager.merge(dataVersionModel);
            entityTransaction.commit();
        }
    }
}
