package org.iatoki.judgels.play.model;

import play.db.jpa.JPA;

import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public abstract class AbstractDao<K, M extends AbstractModel> implements Dao<K, M> {

    private final Class<M> modelClass;

    protected AbstractDao(Class<M> modelClass) {
        this.modelClass = modelClass;
    }

    protected final Class<M> getModelClass() {
        return modelClass;
    }

    protected final M getFirstResultAndDeleteTheRest(CriteriaQuery<M> query) {
        List<M> resultList = JPA.em().createQuery(query).getResultList();
        M result = resultList.get(0);

        for (int i = 1; i < resultList.size(); ++i) {
            remove(resultList.get(i));
        }

        return result;
    }
}
