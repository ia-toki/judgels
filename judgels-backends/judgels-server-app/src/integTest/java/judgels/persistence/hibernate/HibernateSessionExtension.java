package judgels.persistence.hibernate;

import static org.hibernate.cfg.AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS;
import static org.hibernate.cfg.AvailableSettings.DIALECT;
import static org.hibernate.cfg.AvailableSettings.DRIVER;
import static org.hibernate.cfg.AvailableSettings.GENERATE_STATISTICS;
import static org.hibernate.cfg.AvailableSettings.HBM2DDL_AUTO;
import static org.hibernate.cfg.AvailableSettings.URL;

import java.util.UUID;
import org.h2.Driver;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.context.internal.ManagedSessionContext;
import org.hibernate.dialect.H2Dialect;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class HibernateSessionExtension implements ParameterResolver, AfterEachCallback, AfterAllCallback {
    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(SessionFactory.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {

        Configuration config = new Configuration();

        Class<?> declaringClass = parameterContext.getDeclaringExecutable().getDeclaringClass();
        WithHibernateSession annotation = getAnnotation(declaringClass);
        for (Class<?> modelClass : annotation.models()) {
            config.addAnnotatedClass(modelClass);
        }

        config.setProperty(DIALECT, H2Dialect.class.getName());
        config.setProperty(DRIVER, Driver.class.getName());
        config.setProperty(URL, "jdbc:h2:mem:./" + UUID.randomUUID().toString());
        config.setProperty(HBM2DDL_AUTO, "create");
        config.setProperty(CURRENT_SESSION_CONTEXT_CLASS, "managed");
        config.setProperty(GENERATE_STATISTICS, "false");

        SessionFactory sessionFactory = config.buildSessionFactory();
        openSession(sessionFactory, extensionContext);
        return sessionFactory;
    }

    @Override
    public void afterEach(ExtensionContext context) {
        SessionContext sessionContext = getStore(context).get(context.getUniqueId(), SessionContext.class);
        if (sessionContext != null) {
            sessionContext.getSession().close();
            ManagedSessionContext.unbind(sessionContext.getSessionFactory());
            sessionContext.getSessionFactory().close();
        }
    }

    @Override
    public void afterAll(ExtensionContext context) {
        afterEach(context);
    }

    private Store getStore(ExtensionContext context) {
        return context.getStore(Namespace.create(getClass(), context));
    }

    private void openSession(SessionFactory sessionFactory, ExtensionContext context) {
        Session session = sessionFactory.openSession();
        Transaction txn = session.beginTransaction();
        ManagedSessionContext.bind(session);

        SessionContext sessionContext = new SessionContext(sessionFactory, session, txn);
        getStore(context).put(context.getUniqueId(), sessionContext);
    }

    private WithHibernateSession getAnnotation(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        WithHibernateSession annotation = clazz.getAnnotation(WithHibernateSession.class);
        if (annotation != null) {
            return annotation;
        }
        return getAnnotation(clazz.getDeclaringClass());
    }

    private static class SessionContext {
        private final SessionFactory sessionFactory;
        private final Session session;
        private final Transaction txn;

        SessionContext(SessionFactory sessionFactory, Session session, Transaction txn) {
            this.sessionFactory = sessionFactory;
            this.session = session;
            this.txn = txn;
        }

        SessionFactory getSessionFactory() {
            return sessionFactory;
        }

        Session getSession() {
            return session;
        }

        Transaction getTransaction() {
            return txn;
        }
    }
}
