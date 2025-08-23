package judgels.sealtiel.tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.servlets.tasks.Task;
import jakarta.inject.Inject;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import judgels.gabriel.api.GradingRequest;

public class ReceiveGradingRequestTask extends Task {
    private final ObjectMapper mapper;

    @Inject
    public ReceiveGradingRequestTask(ObjectMapper mapper) {
        super("sealtiel-receive-grading-request");

        this.mapper = mapper;
    }

    @Override
    public void execute(Map<String, List<String>> parameters, PrintWriter out) throws Exception {
        List<String> queueNames = parameters.get("queueName");
        if (queueNames == null || queueNames.isEmpty()) {
            mapper.writeValue(out, null);
            return;
        }

        String queueName = queueNames.get(0);

        GradingRequest request = null;

        mapper.writeValue(out, request);
    }
}
