package judgels.jophiel.persistence;

import judgels.persistence.Dao;

public class Daos {
    private Daos() {}

    public interface UserProfileDao extends Dao<UserProfileModel> {}
}
