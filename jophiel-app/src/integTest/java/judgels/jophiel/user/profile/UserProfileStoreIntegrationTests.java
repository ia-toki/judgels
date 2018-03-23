package judgels.jophiel.user.profile;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.jophiel.api.user.profile.UserProfile;
import judgels.jophiel.hibernate.HibernateDaos.UserProfileHibernateDao;
import judgels.jophiel.persistence.Daos.UserProfileDao;
import judgels.jophiel.persistence.UserProfileModel;
import judgels.persistence.FixedActorProvider;
import judgels.persistence.FixedClock;
import judgels.persistence.hibernate.WithHibernateSession;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {UserProfileModel.class})
class UserProfileStoreIntegrationTests {
    private static final String USER_JID = "userJid";

    private UserProfileStore store;

    @BeforeEach
    void before(SessionFactory sessionFactory) {
        UserProfileDao dao = new UserProfileHibernateDao(sessionFactory, new FixedClock(), new FixedActorProvider());
        store = new UserProfileStore(dao);
    }

    @Test
    void can_do_basic_crud() {
        assertThat(store.getUserProfile(USER_JID))
                .isEqualTo(new UserProfile.Builder().build());

        UserProfile userProfile = new UserProfile.Builder()
                .name("First Last")
                .gender("MALE")
                .nationality("id")
                .homeAddress("address")
                .shirtSize("L")
                .institution("university")
                .country("nation")
                .province("province")
                .city("town")
                .build();
        store.upsertUserProfile(USER_JID, userProfile);
        assertThat(store.getUserProfile(USER_JID)).isEqualTo(userProfile);

        UserProfile newUserProfile = new UserProfile.Builder()
                .from(userProfile)
                .gender("FEMALE")
                .build();
        store.upsertUserProfile(USER_JID, newUserProfile);
        assertThat(store.getUserProfile(USER_JID)).isEqualTo(newUserProfile);
    }
}
