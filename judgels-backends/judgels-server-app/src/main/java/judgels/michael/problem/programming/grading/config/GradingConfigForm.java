package judgels.michael.problem.programming.grading.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.FormParam;
import judgels.michael.template.HtmlForm;

public class GradingConfigForm extends HtmlForm {
    static final int DEFAULT_SUBTASK_COUNT = 12;
    static final String HELPER_NONE = "(none)";

    @FormParam("timeLimit")
    int timeLimit;

    @FormParam("memoryLimit")
    int memoryLimit;

    @FormParam("sourceFileFieldKeys")
    String sourceFileFieldKeys;

    @FormParam("testCaseInputs")
    List<String> testCaseInputs = Arrays.asList("", "");

    @FormParam("testCaseOutputs")
    List<String> testCaseOutputs = Arrays.asList("", "");

    @FormParam("sampleTestCaseSubtaskIds")
    List<String> sampleTestCaseSubtaskIds = Collections.emptyList();

    @FormParam("testGroupSubtaskIds")
    List<String> testGroupSubtaskIds = Collections.emptyList();

    @FormParam("subtaskPoints")
    List<Integer> subtaskPoints = Collections.emptyList();

    @FormParam("customScorer")
    String customScorer = HELPER_NONE;

    @FormParam("communicator")
    String communicator = HELPER_NONE;

    public int getTimeLimit() {
        return timeLimit;
    }

    public int getMemoryLimit() {
        return memoryLimit;
    }

    public String getSourceFileFieldKeys() {
        return sourceFileFieldKeys;
    }

    public List<String> getTestCaseInputs() {
        return testCaseInputs;
    }

    public List<String> getTestCaseOutputs() {
        return testCaseOutputs;
    }

    public List<String> getSampleTestCaseSubtaskIds() {
        return sampleTestCaseSubtaskIds;
    }

    public List<String> getTestGroupSubtaskIds() {
        return testGroupSubtaskIds;
    }

    public List<Integer> getSubtaskPoints() {
        return subtaskPoints;
    }

    public String getCustomScorer() {
        return customScorer;
    }

    public String getCommunicator() {
        return communicator;
    }
}
