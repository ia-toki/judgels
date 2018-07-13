package judgels.jophiel.mailer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableMailerConfiguration.class)
public interface MailerConfiguration {
    String getHost();
    int getPort();
    boolean getUseSsl();
    String getUsername();
    String getPassword();
    String getSender();

    class Builder extends ImmutableMailerConfiguration.Builder {}
}
