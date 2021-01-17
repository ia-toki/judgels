package judgels.sandalphon.api.lesson.partner;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableLessonPartner.class)
public interface LessonPartner {
    long getId();
    String getLessonJid();
    String getUserJid();
    LessonPartnerConfig getConfig();

    class Builder extends ImmutableLessonPartner.Builder {}
}
