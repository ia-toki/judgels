package judgels.setting;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.core.JudgelsRequestChecks.checkAllowed;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import java.util.Optional;
import judgels.api.setting.EmailTemplate;
import judgels.api.setting.SettingUpdateData;
import judgels.api.setting.Settings;
import judgels.core.api.AuthHeader;
import judgels.core.mailer.Mailer;
import judgels.core.recaptcha.RecaptchaVerifier;
import judgels.session.ActorChecker;
import judgels.user.UserRoleChecker;

@Path("/api/v2/settings")
public class SettingResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected UserRoleChecker roleChecker;
    @Inject protected SettingStore store;
    @Inject protected Optional<Mailer> mailer;
    @Inject protected Optional<RecaptchaVerifier> recaptchaVerifier;
    @Inject @Named("trainingEnabled") protected boolean trainingEnabled;

    @Inject public SettingResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public Settings getSettings(@HeaderParam(AUTHORIZATION) AuthHeader authHeader) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canAdminister(actorJid));

        return getSettingsWithConfig();
    }

    @PUT
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public Settings updateSettings(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            SettingUpdateData data) {

        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canAdminister(actorJid));
        checkValid(data);

        store.updateSettings(data);

        return getSettingsWithConfig();
    }

    private Settings getSettingsWithConfig() {
        return new Settings.Builder()
                .from(store.getSettings())
                .mailerConfigured(mailer.isPresent())
                .recaptchaConfigured(recaptchaVerifier.isPresent())
                .build();
    }

    private void checkValid(SettingUpdateData data) {
        data.getTrainingAccountRegistration().ifPresent(registration -> {
            checkTrainingEnabled();
            checkMailerConfigured(registration.getEnabled());
            if (registration.getRecaptchaEnabled() && recaptchaVerifier.isEmpty()) {
                throw new BadRequestException("reCAPTCHA cannot be enabled without the recaptcha config");
            }
            checkHasEmailCode(registration.getActivationEmailTemplate());
        });
        data.getTrainingAccountPasswordReset().ifPresent(passwordReset -> {
            checkTrainingEnabled();
            checkMailerConfigured(passwordReset.getEnabled());
            checkHasEmailCode(passwordReset.getRequestEmailTemplate());
        });
    }

    private void checkTrainingEnabled() {
        if (!trainingEnabled) {
            throw new BadRequestException("Training account settings cannot be set while training is disabled");
        }
    }

    private void checkMailerConfigured(boolean enabled) {
        if (enabled && mailer.isEmpty()) {
            throw new BadRequestException("Cannot be enabled without the mailer config");
        }
    }

    private static void checkHasEmailCode(EmailTemplate template) {
        if (!template.getBody().contains("{{emailCode}}")) {
            throw new BadRequestException("Email body must contain {{emailCode}}");
        }
    }
}
