package judgels.michael.problem.programming.grading.config;

import static java.util.stream.Collectors.joining;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import judgels.fs.FileInfo;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.Subtask;
import judgels.gabriel.api.TestCase;
import judgels.gabriel.api.TestGroup;
import liquibase.util.file.FilenameUtils;

public abstract class BaseGradingConfigAdapter implements GradingConfigAdapter {
    protected void fillLimitsFormPartsFromConfig(GradingConfigForm form, GradingConfig config) {
        form.timeLimit = config.getTimeLimit();
        form.memoryLimit = config.getMemoryLimit();
    }

    protected Object[] getLimitsConfigPartsFromForm(GradingConfigForm form) {
        return new Object[]{form.timeLimit, form.memoryLimit};
    }

    protected void fillSourceFileFieldKeysFormPartFromConfig(GradingConfigForm form, List<String> sourceFileFieldKeys) {
        form.sourceFileFieldKeys = String.join(",", sourceFileFieldKeys);
    }

    protected List<String> getSourceFileFieldKeysConfigPartFromForm(GradingConfigForm form) {
        if (form.sourceFileFieldKeys != null) {
            return ImmutableList.copyOf(form.sourceFileFieldKeys.split(","));
        }
        return Collections.emptyList();
    }

    protected void fillTestDataWithoutSubtasksFormPartsFromConfig(GradingConfigForm form, GradingConfig config) {
        form.testCaseInputs = new ArrayList<>();
        form.testCaseOutputs = new ArrayList<>();

        for (TestGroup testGroup : config.getTestData()) {
            form.testCaseInputs.add(testGroup.getTestCases().stream().map(TestCase::getInput).collect(joining(",")));
            form.testCaseOutputs.add(testGroup.getTestCases().stream().map(TestCase::getOutput).collect(joining(",")));
        }
    }

    protected void fillTestDataWithSubtasksFormPartsFromConfig(GradingConfigForm form, GradingConfig config) {
        fillTestDataWithoutSubtasksFormPartsFromConfig(form, config);

        int subtasksCount = Math.max(GradingConfigForm.DEFAULT_SUBTASK_COUNT, config.getSubtasks().size());

        form.sampleTestCaseSubtaskIds = new ArrayList<>();
        form.testGroupSubtaskIds = new ArrayList<>();

        for (TestGroup testGroup : config.getTestData()) {
            if (testGroup.getId() == 0) {
                for (TestCase testCase : testGroup.getTestCases()) {
                    form.sampleTestCaseSubtaskIds.add(testCase.getSubtaskIds().stream()
                            .map(id -> "" + id)
                            .collect(joining(",")));
                }
            } else {
                Set<Integer> subtaskIds = Collections.emptySet();
                if (!testGroup.getTestCases().isEmpty()) {
                    subtaskIds = testGroup.getTestCases().get(0).getSubtaskIds();
                }
                form.testGroupSubtaskIds.add(subtaskIds.stream()
                        .map(id -> "" + id)
                        .collect(joining(",")));
            }
        }

        form.subtaskPoints = new ArrayList<>();
        for (Subtask subtask : config.getSubtasks()) {
            form.subtaskPoints.add(subtask.getPoints());
        }
        for (int i = config.getSubtasks().size(); i < subtasksCount; i++) {
            form.subtaskPoints.add(null);
        }
    }

    private List<TestGroup> getTestDataConfigPartsFromForm(GradingConfigForm form) {
        List<TestGroup> testData = new ArrayList<>();
        for (int i = 0; i < form.testCaseInputs.size(); i++) {
            String[] inputs = form.testCaseInputs.get(i).split(",", -1);
            String[] outputs = form.testCaseOutputs.get(i).split(",", -1);

            List<TestCase> testCases = new ArrayList<>();
            for (int j = 0; j < inputs.length; j++) {
                if (!inputs[j].isEmpty()) {
                    testCases.add(TestCase.of(inputs[j], outputs[j], Collections.emptySet() /* placeholder */));
                }
            }

            testData.add(TestGroup.of(i /* placeholder */, testCases));
        }

        return ImmutableList.copyOf(testData);
    }

    protected List<TestGroup> getTestDataWithoutSubtasksConfigPartsFromForm(GradingConfigForm form) {
        List<TestGroup> testData = getTestDataConfigPartsFromForm(form);
        List<TestGroup> testDataWithoutSubtasks = new ArrayList<>();

        for (TestGroup testGroup : testData) {
            int subtaskId = testGroup.getId() == 0 ? 0 : -1;
            testDataWithoutSubtasks.add(new TestGroup.Builder()
                    .from(testGroup)
                    .id(subtaskId)
                    .testCases(Lists.transform(testGroup.getTestCases(), tc ->
                            new TestCase.Builder()
                                    .from(tc)
                                    .subtaskIds(Collections.singleton(subtaskId))
                                    .build()))
                    .build());
        }

        return ImmutableList.copyOf(testDataWithoutSubtasks);
    }

    protected Object[] getTestDataWithSubtasksConfigPartsFromForm(GradingConfigForm form) {
        List<TestGroup> testData = getTestDataConfigPartsFromForm(form);
        List<TestGroup> testDataWithSubtasks = new ArrayList<>();

        int subtaskCount = 0;

        for (int i = 0; i < testData.size(); i++) {
            TestGroup testGroup = testData.get(i);

            if (testGroup.getId() == 0) {
                List<TestCase> sampleTestCases = new ArrayList<>();

                for (int j = 0; j < testGroup.getTestCases().size(); j++) {
                    Set<Integer> subtaskIds = new LinkedHashSet<>();
                    subtaskIds.add(0);

                    if (j < form.sampleTestCaseSubtaskIds.size()) {
                        for (String subtaskId : form.sampleTestCaseSubtaskIds.get(j).split(",")) {
                            if (!subtaskId.isEmpty()) {
                                subtaskIds.add(Integer.parseInt(subtaskId));
                                subtaskCount = Math.max(subtaskCount, Integer.parseInt(subtaskId));
                            }
                        }
                    }

                    sampleTestCases.add(new TestCase.Builder()
                            .from(testGroup.getTestCases().get(j))
                            .subtaskIds(subtaskIds)
                            .build());
                }

                testDataWithSubtasks.add(new TestGroup.Builder()
                        .from(testGroup)
                        .testCases(sampleTestCases)
                        .build());
            } else {
                Set<Integer> subtaskIds = new LinkedHashSet<>();
                if (i - 1 < form.testGroupSubtaskIds.size()) {
                    for (String subtaskId : form.testGroupSubtaskIds.get(i - 1).split(",")) {
                        if (!subtaskId.isEmpty()) {
                            subtaskIds.add(Integer.parseInt(subtaskId));
                            subtaskCount = Math.max(subtaskCount, Integer.parseInt(subtaskId));
                        }
                    }
                }

                testDataWithSubtasks.add(new TestGroup.Builder()
                        .from(testGroup)
                        .testCases(Lists.transform(testGroup.getTestCases(), tc ->
                                new TestCase.Builder()
                                        .from(tc)
                                        .subtaskIds(subtaskIds)
                                        .build()))
                        .build());
            }
        }

        for (int i = 0; i < form.subtaskPoints.size(); i++) {
            if (form.subtaskPoints.get(i) != null) {
                subtaskCount = Math.max(subtaskCount, i + 1);
            }
        }

        List<Integer> subtaskPoints = new ArrayList<>();
        for (int i = 0; i < subtaskCount; i++) {
            if (i < form.subtaskPoints.size() && form.subtaskPoints.get(i) != null) {
                subtaskPoints.add(form.subtaskPoints.get(i));
            } else {
                subtaskPoints.add(0);
            }
        }

        return new Object[]{ImmutableList.copyOf(testDataWithSubtasks), ImmutableList.copyOf(subtaskPoints)};
    }

    protected void fillCustomScorerFormPartFromConfig(GradingConfigForm form, Optional<String> customScorer) {
        form.customScorer = customScorer.orElse("(none)");
    }

    protected Optional<String> getCustomScorerConfigPartFromForm(GradingConfigForm form) {
        if (!form.customScorer.equals("(none)")) {
            return Optional.of(form.customScorer);
        }
        return Optional.empty();
    }

    protected void fillCommunicatorFormPartFromConfig(GradingConfigForm form, Optional<String> communicator) {
        form.communicator = communicator.orElse("(none)");
    }

    protected Optional<String> getCommunicatorConfigPartFromForm(GradingConfigForm form) {
        if (!form.communicator.equals("(none)")) {
            return Optional.of(form.communicator);
        }
        return Optional.empty();
    }

    protected List<TestGroup> autoPopulateTestDataByFilename(List<FileInfo> testDataFiles) {
        List<TestCase> testCases = new ArrayList<>();
        List<TestCase> sampleTestCases = new ArrayList<>();

        int i;
        for (i = 0; i + 1 < testDataFiles.size(); i++) {
            String in = testDataFiles.get(i).getName();
            String out = testDataFiles.get(i + 1).getName();
            if (isTestCasePair(in, out)) {
                if (in.contains("sample")) {
                    sampleTestCases.add(TestCase.of(in, out, ImmutableSet.of(0)));
                } else {
                    testCases.add(TestCase.of(in, out, ImmutableSet.of(-1)));
                }
                i++;
            }
        }

        return ImmutableList.of(
                TestGroup.of(0, sampleTestCases),
                TestGroup.of(-1, testCases));
    }

    protected Object[] autoPopulateTestDataByTCFrameFormat(List<Subtask> subtasks, List<FileInfo> testDataFiles) {
        Set<String> filenames = new HashSet<>(Lists.transform(testDataFiles, f -> f.getName()));
        Set<String> filenamesNoExt = new HashSet<>();
        for (String filename : filenames) {
            String[] parts = filename.split("\\.");
            if (parts.length != 2) {
                continue;
            }

            filenamesNoExt.add(parts[0]);
        }

        List<TCFrameFile> tcframeFiles = new ArrayList<>();

        for (String filename : filenamesNoExt) {
            if (!filenames.contains(filename + ".in") || !filenames.contains(filename + ".out")) {
                continue;
            }

            String[] parts = filename.split("_");
            if (parts.length != 3) {
                continue;
            }

            try {
                String name = parts[0];
                int tgNo;
                if (parts[1].equals("sample")) {
                    tgNo = 0;
                } else {
                    tgNo = Integer.parseInt(parts[1]);
                }
                int tcNo = Integer.parseInt(parts[2]);

                tcframeFiles.add(new TCFrameFile(name, tgNo, tcNo));
            } catch (NumberFormatException e) {
                // skip
            }
        }

        Collections.sort(tcframeFiles);

        int maxTgNo = 0;
        for (TCFrameFile file : tcframeFiles) {
            maxTgNo = Math.max(maxTgNo, file.tgNo);
        }

        List<List<TestCase>> testGroups = new ArrayList<>();
        for (int i = 0; i <= maxTgNo; i++) {
            testGroups.add(new ArrayList<>());
        }

        for (TCFrameFile file : tcframeFiles) {
            String name = file.filename;
            int tgNo = file.tgNo;
            String tgName = tgNo == 0 ? "sample" : "" + tgNo;
            int tcNo = file.tcNo;

            String filename = name + "_" + tgName + "_" + tcNo;
            Set<Integer> subtaskIds = new LinkedHashSet<>();

            if (tgNo == 0) {
                subtaskIds.add(0);
            } else {
                for (int i = tgNo; i <= maxTgNo; i++) {
                    subtaskIds.add(i);
                }
            }

            TestCase testCase = TestCase.of(filename + ".in", filename + ".out", subtaskIds);

            testGroups.get(file.tgNo).add(testCase);
        }

        List<TestGroup> testData = new ArrayList<>();
        for (int i = 0; i <= maxTgNo; i++) {
            testData.add(TestGroup.of(i, testGroups.get(i)));
        }

        List<Integer> subtaskPoints = new ArrayList<>();
        for (int i = 0; i < maxTgNo; i++) {
            if (i < subtasks.size()) {
                subtaskPoints.add(subtasks.get(i).getPoints());
            } else {
                subtaskPoints.add(0);
            }
        }

        return new Object[]{testData, subtaskPoints};
    }

    private boolean isTestCasePair(String in, String out) {
        String inBaseName = FilenameUtils.getBaseName(in);
        String inExtension = FilenameUtils.getExtension(in);

        String outBaseName = FilenameUtils.getBaseName(out);
        String outExtension = FilenameUtils.getExtension(out);

        return inBaseName.equals(outBaseName) && inExtension.equals("in") && outExtension.equals("out");
    }
}
