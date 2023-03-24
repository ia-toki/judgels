package judgels.michael.problem.programming.grading.config;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import judgels.gabriel.api.TestCase;
import judgels.gabriel.api.TestGroup;

public abstract class SingleSourceFileWithoutSubtasksGradingConfigAdapter extends BaseGradingConfigAdapter {
    protected Object[] getSingleSourceFileWithoutSubtasksConfigPartsFromForm(GradingConfigForm form) {
        Object[] parts = getConfigPartsFromForm(form);

        @SuppressWarnings("unchecked")
        List<TestGroup> testData = (List<TestGroup>) parts[2];

        List<TestGroup> testDataWithSubtasks = new ArrayList<>();
        for (TestGroup testGroup : testData) {
            int subtaskId = testGroup.getId() == 0 ? 0 : -1;
            testDataWithSubtasks.add(new TestGroup.Builder()
                    .from(testGroup)
                    .id(subtaskId)
                    .testCases(Lists.transform(testGroup.getTestCases(), tc ->
                            new TestCase.Builder()
                                    .from(tc)
                                    .subtaskIds(Collections.singleton(subtaskId))
                                    .build()))
                    .build());
        }

        return new Object[]{parts[0], parts[1], testDataWithSubtasks};
    }
}
