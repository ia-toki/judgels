package org.iatoki.judgels.play.jedis;

import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import org.iatoki.judgels.play.model.AbstractJedisHibernateDao;
import org.iatoki.judgels.play.model.AbstractJudgelsJedisHibernateDao;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.Set;

public final class JedisModule extends AbstractModule {

    private static final Set<Class<?>> JEDIS_BASE_CLASSES = ImmutableSet.of(
            AbstractJedisHibernateDao.class,
            AbstractJudgelsJedisHibernateDao.class
    );

    @Override
    protected void configure() {
        Reflections reflections = new Reflections("org.iatoki.judgels");

        for (Class<?> jedisBaseClass : JEDIS_BASE_CLASSES) {
            for (Class<?> implClass : reflections.getSubTypesOf(jedisBaseClass)) {
                if (Modifier.isFinal(implClass.getModifiers())) {
                    for (Class<?> daoClass : implClass.getInterfaces()) {
                        bind(daoClass).to(daoClass.getClass().cast(implClass));
                    }
                }
            }
        }
    }
}
