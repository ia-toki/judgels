package judgels.michael.problem.programming.grading.config;

import java.util.Arrays;
import java.util.List;
import javax.ws.rs.FormParam;
import judgels.michael.template.HtmlForm;

public class GradingConfigForm extends HtmlForm {
    @FormParam("timeLimit")
    int timeLimit;

    @FormParam("memoryLimit")
    int memoryLimit;

    @FormParam("testCaseInputs")
    List<String> testCaseInputs = Arrays.asList("", "");

    @FormParam("testCaseOutputs")
    List<String> testCaseOutputs = Arrays.asList("", "");

    @FormParam("customScorer")
    String customScorer;

    public int getTimeLimit() {
        return timeLimit;
    }

    public int getMemoryLimit() {
        return memoryLimit;
    }

    public List<String> getTestCaseInputs() {
        return testCaseInputs;
    }

    public List<String> getTestCaseOutputs() {
        return testCaseOutputs;
    }

    public String getCustomScorer() {
        return customScorer;
    }
}
