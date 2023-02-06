package org.iatoki.judgels.play.model;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.Query;
import javax.persistence.SynchronizationType;
import javax.persistence.criteria.CriteriaBuilder;
import org.hibernate.Cache;
import org.hibernate.HibernateException;
import org.hibernate.Metamodel;
import org.hibernate.Session;
import org.hibernate.SessionBuilder;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.StatelessSessionBuilder;
import org.hibernate.TypeHelper;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.stat.Statistics;
import play.db.jpa.JPAApi;

public class PlaySessionFactory implements SessionFactory {
    @Inject
    private JPAApi jpaApi;

    @Override
    public SessionFactoryOptions getSessionFactoryOptions() {
        return null;
    }

    @Override
    public SessionBuilder withOptions() {
        return null;
    }

    @Override
    public Session openSession() throws HibernateException {
        return null;
    }

    @Override
    public Session getCurrentSession() throws HibernateException {
        return jpaApi.em().unwrap(Session.class);
    }

    @Override
    public StatelessSessionBuilder withStatelessOptions() {
        return null;
    }

    @Override
    public StatelessSession openStatelessSession() {
        return null;
    }

    @Override
    public StatelessSession openStatelessSession(Connection connection) {
        return null;
    }

    @Override
    public Statistics getStatistics() {
        return null;
    }

    @Override
    public void close() throws HibernateException {

    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public Cache getCache() {
        return null;
    }

    @Override
    public Set getDefinedFilterNames() {
        return null;
    }

    @Override
    public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {
        return null;
    }

    @Override
    public boolean containsFetchProfileDefinition(String name) {
        return false;
    }

    @Override
    public TypeHelper getTypeHelper() {
        return null;
    }

    @Override
    public ClassMetadata getClassMetadata(Class entityClass) {
        return null;
    }

    @Override
    public ClassMetadata getClassMetadata(String entityName) {
        return null;
    }

    @Override
    public CollectionMetadata getCollectionMetadata(String roleName) {
        return null;
    }

    @Override
    public Map<String, ClassMetadata> getAllClassMetadata() {
        return null;
    }

    @Override
    public Map getAllCollectionMetadata() {
        return null;
    }

    @Override
    public Reference getReference() throws NamingException {
        return null;
    }

    @Override
    public <T> List<EntityGraph<? super T>> findEntityGraphsByType(Class<T> entityClass) {
        return null;
    }

    @Override
    public Metamodel getMetamodel() {
        return null;
    }

    @Override
    public EntityManager createEntityManager() {
        return null;
    }

    @Override
    public EntityManager createEntityManager(Map map) {
        return null;
    }

    @Override
    public EntityManager createEntityManager(SynchronizationType synchronizationType) {
        return null;
    }

    @Override
    public EntityManager createEntityManager(SynchronizationType synchronizationType, Map map) {
        return null;
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return null;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public Map<String, Object> getProperties() {
        return null;
    }

    @Override
    public PersistenceUnitUtil getPersistenceUnitUtil() {
        return null;
    }

    @Override
    public void addNamedQuery(String name, Query query) {

    }

    @Override
    public <T> T unwrap(Class<T> cls) {
        return null;
    }

    @Override
    public <T> void addNamedEntityGraph(String graphName, EntityGraph<T> entityGraph) {

    }
}
