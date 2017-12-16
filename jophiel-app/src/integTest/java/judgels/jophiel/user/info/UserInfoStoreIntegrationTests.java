package judgels.jophiel.user.info;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.jophiel.api.user.UserInfo;
import judgels.jophiel.hibernate.UserInfoHibernateDao;
import judgels.persistence.FixedActorProvider;
import judgels.persistence.FixedClock;
import judgels.persistence.hibernate.WithHibernateSession;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {UserInfoModel.class})
class UserInfoStoreIntegrationTests {
    private static final String USER_JID = "userJid";

    private UserInfoStore store;

    @BeforeEach void before(SessionFactory sessionFactory) {
        UserInfoDao dao = new UserInfoHibernateDao(sessionFactory, new FixedClock(), new FixedActorProvider());
        store = new UserInfoStore(dao);
    }

    @Test void can_do_basic_crud() {
        assertThat(store.getUserInfo(USER_JID))
                .isEqualTo(new UserInfo.Builder().build());

        UserInfo userInfo = new UserInfo.Builder()
                .name("First Last")
                .gender("MALE")
                .streetAddress("address")
                .postalCode("code")
                .institution("university")
                .city("town")
                .provinceOrState("province")
                .country("nation")
                .shirtSize("L")
                .build();
        store.upsertUserInfo(USER_JID, userInfo);
        assertThat(store.getUserInfo(USER_JID)).isEqualTo(userInfo);

        UserInfo newUserInfo = new UserInfo.Builder()
                .from(userInfo)
                .gender("FEMALE")
                .build();
        store.upsertUserInfo(USER_JID, newUserInfo);
        assertThat(store.getUserInfo(USER_JID)).isEqualTo(newUserInfo);
    }
}
