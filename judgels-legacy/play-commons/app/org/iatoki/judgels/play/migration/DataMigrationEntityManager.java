package org.iatoki.judgels.play.migration;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public final class DataMigrationEntityManager {

    private DataMigrationEntityManager() {

    }

    public static EntityManager createEntityManager() {
        return Persistence.createEntityManagerFactory("migrationPersistenceUnit").createEntityManager();
    }
}
