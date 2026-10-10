package judgels.catalog.problem.programming.grading;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import judgels.core.fs.FileInfo;
import judgels.grading.api.Subtask;
import judgels.grading.api.TestCase;
import judgels.grading.api.TestGroup;
import org.apache.commons.io.FilenameUtils;

/**
 * An engine without subtasks pairs each {@code X.in} with its {@code X.out}, and takes the pairs
 * whose name contains {@code sample} as samples. An engine with subtasks reads the TCFrame format,
 * {@code <slug>_<group or "sample">_<case>.in}, and falls back to pairing with a single subtask.
 */
public abstract class BaseGradingConfigAutoPopulator implements GradingConfigAutoPopulator {
    protected List<TestGroup> autoPopulateTestDataByFilename(boolean hasOutput, List<FileInfo> testDataFiles) {
        List<TestCase> testCases = new ArrayList<>();
        List<TestCase> sampleTestCases = new ArrayList<>();

        int i;
        for (i = 0; i + (hasOutput ? 1 : 0) < testDataFiles.size(); i++) {
            String in = testDataFiles.get(i).getName();
            String out = hasOutput ? testDataFiles.get(i + 1).getName() : "";
            if (isTestCasePair(in, out)) {
                if (in.contains("sample")) {
                    sampleTestCases.add(TestCase.of(in, out, ImmutableSet.of(0)));
                } else {
                    testCases.add(TestCase.of(in, out, ImmutableSet.of(-1)));
                }
                i += (hasOutput ? 1 : 0);
            }
        }

        return ImmutableList.of(
                TestGroup.of(0, sampleTestCases),
                TestGroup.of(-1, testCases));
    }

    protected Object[] autoPopulateTestDataByTCFrameFormat(
            boolean hasOutput,
            List<Subtask> subtasks,
            List<FileInfo> testDataFiles) {
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
            if (!filenames.contains(filename + ".in") || (hasOutput && !filenames.contains(filename + ".out"))) {
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

            TestCase testCase = TestCase.of(filename + ".in", hasOutput ? filename + ".out" : "", subtaskIds);

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

        if (maxTgNo == 0) {
            // At this point, the provided test cases don't actually follow TCFrame format.
            // We fall back to treating this problem to have a single subtask.

            List<TestGroup> testGroupsPopulatedByFilename = autoPopulateTestDataByFilename(hasOutput, testDataFiles);

            List<TestGroup> testGroupsWithSubtask1 = new ArrayList<>();
            for (TestGroup testGroup : testGroupsPopulatedByFilename) {
                int testGroupId = testGroup.getId() == 0 ? 0 : 1;

                List<TestCase> testCasesForSubtask1 = new ArrayList<>();
                for (TestCase testCase : testGroup.getTestCases()) {
                    if (testGroupId == 0) {
                        testCasesForSubtask1.add(new TestCase.Builder()
                                .from(testCase)
                                .subtaskIds(Set.of(0, 1))
                                .build());
                    } else {
                        testCasesForSubtask1.add(new TestCase.Builder()
                                .from(testCase)
                                .subtaskIds(Set.of(1))
                                .build());
                    }
                }

                testGroupsWithSubtask1.add(new TestGroup.Builder()
                        .id(testGroupId)
                        .testCases(testCasesForSubtask1)
                        .build());
            }

            List<Integer> subtask1Points = List.of(100);
            return new Object[]{testGroupsWithSubtask1, subtask1Points};
        }

        return new Object[]{testData, subtaskPoints};
    }

    private static boolean isTestCasePair(String in, String out) {
        String inBaseName = FilenameUtils.getBaseName(in);
        String inExtension = FilenameUtils.getExtension(in);

        if (out.isEmpty()) {
            return inExtension.equals("in");
        }

        String outBaseName = FilenameUtils.getBaseName(out);
        String outExtension = FilenameUtils.getExtension(out);

        return inBaseName.equals(outBaseName) && inExtension.equals("in") && outExtension.equals("out");
    }
}
