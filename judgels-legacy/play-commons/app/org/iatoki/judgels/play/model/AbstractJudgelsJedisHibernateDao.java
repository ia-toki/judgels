package org.iatoki.judgels.play.model;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import play.db.jpa.JPA;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.List;

public abstract class AbstractJudgelsJedisHibernateDao<M extends AbstractJudgelsModel> extends AbstractJudgelsHibernateDao<M> implements JudgelsDao<M> {

    private final JedisPool jedisPool;

    protected AbstractJudgelsJedisHibernateDao(JedisPool jedisPool, Class<M> modelClass) {
        super(modelClass);
        this.jedisPool = jedisPool;
    }

    @Override
    public void persist(M model, String user, String ipAddress) {
        super.persist(model, user, ipAddress);

        String jsonModel = new Gson().toJson(model);
        Jedis jedis = jedisPool.getResource();
        jedis.set(getModelClass().getCanonicalName() + model.id, jsonModel);
        jedis.set(getModelClass().getCanonicalName() + model.jid, jsonModel);
        jedisPool.returnResource(jedis);
    }

    @Override
    public void persist(M model, int childIndex, String user, String ipAddress) {
        super.persist(model, user, ipAddress);

        String jsonModel = new Gson().toJson(model);
        Jedis jedis = jedisPool.getResource();
        jedis.set(getModelClass().getCanonicalName() + model.id, jsonModel);
        jedis.set(getModelClass().getCanonicalName() + model.jid, jsonModel);
        jedisPool.returnResource(jedis);
    }

    @Override
    public M edit(M model, String user, String ipAddress) {
        M ret = super.edit(model, user, ipAddress);

        String jsonModel = new Gson().toJson(ret);
        Jedis jedis = jedisPool.getResource();
        jedis.set(getModelClass().getCanonicalName() + model.id, jsonModel);
        jedis.set(getModelClass().getCanonicalName() + model.jid, jsonModel);
        jedisPool.returnResource(jedis);
        return ret;
    }

    @Override
    public final void remove(M model) {
        Jedis jedis = jedisPool.getResource();
        jedis.del(getModelClass().getCanonicalName() + model.id);
        jedis.del(getModelClass().getCanonicalName() + model.jid);
        jedisPool.returnResource(jedis);

        super.remove(model);
    }

    @Override
    public final boolean existsById(Long id) {
        Jedis jedis = jedisPool.getResource();
        if (jedis.exists(getModelClass().getCanonicalName() + id)) {
            jedisPool.returnResource(jedis);
            return true;
        }

        jedisPool.returnResource(jedis);
        return super.existsById(id);
    }

    @Override
    public final M findById(Long id) {
        Jedis jedis = jedisPool.getResource();
        if (jedis.exists(getModelClass().getCanonicalName() + id)) {
            M model = new Gson().fromJson(jedis.get(getModelClass().getCanonicalName() + id), getModelClass());
            jedisPool.returnResource(jedis);
            return  model;
        }

        M model = super.findById(id);
        String jsonModel = new Gson().toJson(model);
        jedis.set(getModelClass().getCanonicalName() + model.id, jsonModel);
        jedis.set(getModelClass().getCanonicalName() + model.jid, jsonModel);

        jedisPool.returnResource(jedis);
        return model;
    }

    @Override
    public final boolean existsByJid(String jid) {
        Jedis jedis = jedisPool.getResource();
        if (jedis.exists(getModelClass().getCanonicalName() + jid)) {
            jedisPool.returnResource(jedis);
            return true;
        }

        jedisPool.returnResource(jedis);
        return super.existsByJid(jid);
    }

    @Override
    public final M findByJid(String jid) {
        Jedis jedis = jedisPool.getResource();
        if (jedis.exists(getModelClass().getCanonicalName() + jid)) {
            return new Gson().fromJson(jedis.get(getModelClass().getCanonicalName() + jid), getModelClass());
        }

        M model = super.findByJid(jid);

        jedis.set(getModelClass().getCanonicalName() + jid, new Gson().toJson(model));

        jedisPool.returnResource(jedis);
        return model;
    }

    @Override
    public final List<M> getAll() {
        Jedis jedis = jedisPool.getResource();
        List<M> results = super.getAll();

        for (M model : results) {
            String jsonModel = new Gson().toJson(model);
            jedis.set(getModelClass().getCanonicalName() + model.id, jsonModel);
            jedis.set(getModelClass().getCanonicalName() + model.jid, jsonModel);
        }

        jedisPool.returnResource(jedis);
        return results;
    }

    @Override
    public List<M> getByJids(Collection<String> jids) {
        Jedis jedis = jedisPool.getResource();
        List<String> queriedJids;
        ImmutableList.Builder<M> resultsBuilder = ImmutableList.builder();

        ImmutableList.Builder<String> queriedJidsBuilder = ImmutableList.builder();
        for (String jid : jids) {
            if (jedis.exists(getModelClass().getCanonicalName() + jid)) {
                resultsBuilder.add(new Gson().fromJson(jedis.get(getModelClass().getCanonicalName() + jid), getModelClass()));
            } else {
                queriedJidsBuilder.add(jid);
            }
        }
        queriedJids = queriedJidsBuilder.build();

        resultsBuilder.addAll(processGetByJids(queriedJids));

        jedisPool.returnResource(jedis);
        return resultsBuilder.build();
    }

    private List<M> processGetByJids(Collection<String> jids) {
        if (jids.isEmpty()) {
            return ImmutableList.of();
        }

        Jedis jedis = jedisPool.getResource();
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<M> query = cb.createQuery(getModelClass());

        Root<M> root = query.from(getModelClass());

        query.where(root.get(AbstractJudgelsModel_.jid).in(jids));

        List<M> results = JPA.em().createQuery(query).getResultList();

        for (M model : results) {
            String jsonModel = new Gson().toJson(model);
            jedis.set(getModelClass().getCanonicalName() + model.id, jsonModel);
            jedis.set(getModelClass().getCanonicalName() + model.jid, jsonModel);
        }

        jedisPool.returnResource(jedis);
        return results;
    }
}
