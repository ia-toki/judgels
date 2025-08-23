package judgels.sealtiel.tasks;

import io.dropwizard.servlets.tasks.Task;
import jakarta.inject.Inject;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class SendGradingResponseTask extends Task {
    @Inject
    public SendGradingResponseTask() {
        super("sealtiel-send-grading-response");
    }

    @Override
    public void execute(Map<String, List<String>> parameters, PrintWriter out) throws Exception {

    }
}
