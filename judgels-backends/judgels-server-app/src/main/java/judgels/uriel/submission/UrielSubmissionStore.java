package judgels.uriel.submission;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.inject.Qualifier;
import java.lang.annotation.Retention;

@Qualifier
@Retention(RUNTIME)
public @interface UrielSubmissionStore {}
