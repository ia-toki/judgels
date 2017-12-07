package judgels.jophiel.user.email;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import judgels.jophiel.hibernate.UserVerificationEmailHibernateDao;
import judgels.persistence.FixedActorProvider;
import judgels.persistence.FixedClock;
import judgels.persistence.hibernate.WithHibernateSession;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {UserVerificationEmailModel.class})
class UserVerificationEmailStoreIntegrationTests {
    private static final String USER_JID = "userJid";

    private UserVerificationEmailStore store;

    @BeforeEach void before(SessionFactory sessionFactory) {
        UserVerificationEmailDao dao =
                new UserVerificationEmailHibernateDao(sessionFactory, new FixedClock(), new FixedActorProvider());
        store = new UserVerificationEmailStore(dao);
    }

    @Test void user_with_missing_entry_is_considered_verified() {
        assertThat(store.isUserVerified(USER_JID)).isTrue();
    }

    @Test void cannot_verify_nonexistent_email_code() {
        assertThat(store.verifyEmailCode("nonexistent")).isFalse();
    }

    @Test void can_generate_email_code_and_verify() {
        String emailCode = store.generateEmailCode(USER_JID);
        assertThat(store.isUserVerified(USER_JID)).isFalse();

        assertThat(store.verifyEmailCode(emailCode)).isTrue();
        assertThat(store.isUserVerified(USER_JID)).isTrue();

        assertThat(store.verifyEmailCode(emailCode)).isFalse();
    }
}
