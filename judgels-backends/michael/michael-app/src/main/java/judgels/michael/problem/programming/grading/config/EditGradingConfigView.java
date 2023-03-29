package judgels.michael.problem.programming.grading.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import judgels.fs.FileInfo;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;

public class EditGradingConfigView extends TemplateView {
    private final List<FileInfo> testDataFiles;
    private final List<FileInfo> helperFiles;
    private final boolean canEdit;

    public EditGradingConfigView(
            String engine,
            HtmlTemplate template,
            GradingConfigForm form,
            List<FileInfo> testDataFiles,
            List<FileInfo> helperFiles,
            boolean canEdit) {

        super(engine + "GradingConfigView.ftl", template, form);
        this.testDataFiles = testDataFiles;
        this.helperFiles = helperFiles;
        this.canEdit = canEdit;
    }

    public List<FileInfo> getTestDataFiles() {
        return testDataFiles;
    }

    public List<FileInfo> getHelperFiles() {
        return helperFiles;
    }

    public Map<String, String> getHelperFilenamesForCustomScorer() {
        Map<String, String> filenames = new LinkedHashMap<>();
        filenames.put("(none)", "(None)");
        for (FileInfo file : helperFiles) {
            filenames.put(file.getName(), file.getName());
        }
        return filenames;
    }

    public Map<String, String> getHelperFilenamesForCommunicator() {
        Map<String, String> filenames = new LinkedHashMap<>();
        if (helperFiles.isEmpty()) {
            filenames.put("(none)", "(None yet. Please add communicator as a helper file.)");
        } else {
            for (FileInfo file : helperFiles) {
                filenames.put(file.getName(), file.getName());
            }
        }
        return filenames;
    }

    public boolean getCanEdit() {
        return canEdit;
    }

    public int getDefaultSubtaskCount() {
        return GradingConfigForm.DEFAULT_SUBTASK_COUNT;
    }
}
