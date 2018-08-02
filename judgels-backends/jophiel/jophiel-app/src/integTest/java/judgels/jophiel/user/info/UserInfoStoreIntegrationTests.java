package judgels.jophiel.user.info;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.jophiel.DaggerJophielIntegrationTestComponent;
import judgels.jophiel.JophielIntegrationTestComponent;
import judgels.jophiel.JophielIntegrationTestHibernateModule;
import judgels.jophiel.api.user.info.UserInfo;
import judgels.jophiel.persistence.UserInfoModel;
import judgels.persistence.hibernate.WithHibernateSession;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {UserInfoModel.class})
class UserInfoStoreIntegrationTests {
    private static final String USER_JID = "userJid";

    private UserInfoStore store;

    @BeforeEach
    void before(SessionFactory sessionFactory) {
        JophielIntegrationTestComponent component = DaggerJophielIntegrationTestComponent.builder()
                .jophielIntegrationTestHibernateModule(new JophielIntegrationTestHibernateModule(sessionFactory))
                .build();
        store = component.userInfoStore();
    }

    @Test
    void can_do_basic_crud() {
        assertThat(store.getInfo(USER_JID))
                .isEqualTo(new UserInfo.Builder().build());

        UserInfo profile = new UserInfo.Builder()
                .name("First Last")
                .gender("MALE")
                .country("id")
                .homeAddress("address")
                .shirtSize("L")
                .institutionName("university")
                .institutionCountry("nation")
                .institutionProvince("province")
                .institutionCity("town")
                .build();
        store.upsertInfo(USER_JID, profile);
        assertThat(store.getInfo(USER_JID)).isEqualTo(profile);

        UserInfo newUserInfo = new UserInfo.Builder()
                .from(profile)
                .gender("FEMALE")
                .build();
        store.upsertInfo(USER_JID, newUserInfo);
        assertThat(store.getInfo(USER_JID)).isEqualTo(newUserInfo);
    }
}
