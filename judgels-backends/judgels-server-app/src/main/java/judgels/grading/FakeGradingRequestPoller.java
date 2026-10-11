package judgels.grading;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.lifecycle.Managed;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ThreadPoolExecutor;
import judgels.core.JudgelsObjectMappers;
import judgels.core.messaging.MessageClient;
import judgels.core.messaging.MessageListener;
import judgels.core.messaging.api.Message;
import judgels.grading.api.GradingRequest;
import judgels.grading.api.GradingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FakeGradingRequestPoller implements Managed {
    private static final ObjectMapper MAPPER = JudgelsObjectMappers.OBJECT_MAPPER;
    private static final Logger LOGGER = LoggerFactory.getLogger(FakeGradingRequestPoller.class);

    private final MessageListener messageListener;
    private final MessageClient messageClient;
    private final String queueName;
    private final ThreadPoolExecutor executorService;
    private final Path baseDataDir;
    private final FakeGrader grader;

    public FakeGradingRequestPoller(
            MessageListener messageListener,
            MessageClient messageClient,
            String queueName,
            ThreadPoolExecutor executorService,
            Path baseDataDir,
            FakeGrader grader) {

        this.messageListener = messageListener;
        this.messageClient = messageClient;
        this.queueName = queueName;
        this.executorService = executorService;
        this.baseDataDir = baseDataDir;
        this.grader = grader;
    }

    @Override
    public void start() throws Exception {
        try {
            messageListener.start(queueName, executorService, this::process);
        } catch (Exception e) {
            LOGGER.error("Failed to run fake grading request poller", e);
            throw e;
        }
    }

    @Override
    public void stop() {
        messageListener.stop();
    }

    private void process(Message message) {
        try {
            GradingRequest request = MAPPER.readValue(message.getContent(), GradingRequest.class);
            GradingResponse response = grader.grade(request, readGradingEngine(request.getProblemJid()));

            messageClient.sendMessage(
                    queueName,
                    message.getSourceQueueName(),
                    GradingResponse.class.getSimpleName(),
                    MAPPER.writeValueAsString(response));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String readGradingEngine(String problemJid) throws IOException {
        Path engineFile = baseDataDir.resolve("problems").resolve(problemJid).resolve("grading").resolve("engine.txt");
        return Files.readString(engineFile, StandardCharsets.UTF_8).trim();
    }
}
