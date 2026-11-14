package judgels.api.training.account.registration;

import jakarta.ws.rs.core.Response.Status;
import judgels.core.api.JudgelsApiException;

public class AccountRegistrationErrors {
    private AccountRegistrationErrors() {}

    public static final String USERNAME_ALREADY_EXISTS = "AccountRegistrationUsernameAlreadyExists";
    public static final String EMAIL_ALREADY_EXISTS = "AccountRegistrationEmailAlreadyExists";

    public static JudgelsApiException usernameAlreadyExists() {
        return new JudgelsApiException(Status.BAD_REQUEST, USERNAME_ALREADY_EXISTS);
    }

    public static JudgelsApiException emailAlreadyExists() {
        return new JudgelsApiException(Status.BAD_REQUEST, EMAIL_ALREADY_EXISTS);
    }
}
