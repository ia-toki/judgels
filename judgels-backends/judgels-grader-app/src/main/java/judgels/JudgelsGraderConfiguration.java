package judgels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.nio.file.Path;
import java.util.Optional;
import judgels.grading.JudgelsGraderGradingConfiguration;
import judgels.isolate.IsolateConfiguration;
import judgels.messaging.rabbitmq.RabbitMQConfiguration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableJudgelsGraderConfiguration.class)
public interface JudgelsGraderConfiguration {
    Path getBaseDataDir();

    @JsonProperty("rabbitmq")
    RabbitMQConfiguration getRabbitMQConfig();

    @JsonProperty("grading")
    JudgelsGraderGradingConfiguration getGradingConfig();

    @JsonProperty("isolate")
    Optional<IsolateConfiguration> getIsolateConfig();
}
