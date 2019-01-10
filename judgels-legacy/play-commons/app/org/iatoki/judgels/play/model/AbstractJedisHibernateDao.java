package org.iatoki.judgels.play.model;

import com.google.gson.Gson;
import play.db.jpa.JPA;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.util.List;

public abstract class AbstractJedisHibernateDao<K, M extends AbstractModel> extends AbstractHibernateDao<K, M> {

    private final JedisPool jedisPool;

    protected AbstractJedisHibernateDao(JedisPool jedisPool, Class<M> modelClass) {
        super(modelClass);
        this.jedisPool = jedisPool;
    }

    @Override
    public void persist(M model, String user, String ipAddress) {
        super.persist(model, user, ipAddress);

        Jedis jedis = jedisPool.getResource();
        jedis.set(jedisKey(model), new Gson().toJson(model));
        jedisPool.returnResource(jedis);
    }

    @Override
    public M edit(M model, String user, String ipAddress) {
        M ret = super.edit(model, user, ipAddress);

        Jedis jedis = jedisPool.getResource();
        jedis.set(jedisKey(model), new Gson().toJson(ret));
        jedisPool.returnResource(jedis);
        return ret;
    }

    @Override
    public void remove(M model) {
        Jedis jedis = jedisPool.getResource();
        jedis.del(jedisKey(model));
        jedisPool.returnResource(jedis);

        JPA.em().remove(model);
    }

    @Override
    public final boolean existsById(K id) {
        Jedis jedis = jedisPool.getResource();
        if (jedis.exists(getModelClass().getCanonicalName() + id)) {
            jedisPool.returnResource(jedis);
            return true;
        }

        jedisPool.returnResource(jedis);
        return super.existsById(id);
    }

    @Override
    public final M findById(K id) {
        Jedis jedis = jedisPool.getResource();
        if (jedis.exists(getModelClass().getCanonicalName() + id)) {
            jedisPool.returnResource(jedis);
            return new Gson().fromJson(jedis.get(getModelClass().getCanonicalName() + id), getModelClass());
        }

        M result = super.findById(id);
        jedis.set(getModelClass().getCanonicalName() + id, new Gson().toJson(result));

        jedisPool.returnResource(jedis);
        return result;
    }

    @Override
    public final List<M> getAll() {
        Jedis jedis = jedisPool.getResource();
        List<M> models = super.getAll();

        for (M model : models) {
            jedis.set(jedisKey(model), new Gson().toJson(model));
        }

        jedisPool.returnResource(jedis);
        return models;
    }

    private String jedisKey(M model) {
        try {
            for (Field field : getModelClass().getFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    return getModelClass().getCanonicalName() + field.get(model);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return "";
    }
}
