package org.iatoki.judgels.sandalphon.problem.programming;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.FileInfo;
import org.iatoki.judgels.gabriel.GradingConfig;
import org.iatoki.judgels.sandalphon.problem.programming.grading.LanguageRestriction;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@ImplementedBy(ProgrammingProblemServiceImpl.class)
public interface ProgrammingProblemService {

    void initProgrammingProblem(String problemJid, String gradingEngine) throws IOException;

    GradingConfig getGradingConfig(String userJid, String problemJid) throws IOException;

    void updateGradingConfig(String userJid, String problemJid, GradingConfig gradingConfig) throws IOException;

    Date getGradingLastUpdateTime(String userJid, String problemJid) throws IOException;

    String getGradingEngine(String userJid, String problemJid) throws IOException;

    void updateGradingEngine(String userJid, String problemJid, String gradingEngine) throws IOException;

    LanguageRestriction getLanguageRestriction(String userJid, String problemJid) throws IOException;

    void updateLanguageRestriction(String userJid, String problemJid, LanguageRestriction languageRestriction) throws IOException;

    void uploadGradingTestDataFile(String userJid, String problemJid, File testDataFile, String filename) throws IOException;

    void uploadGradingTestDataFileZipped(String userJid, String problemJid, File testDataFileZipped) throws IOException;

    void uploadGradingHelperFile(String userJid, String problemJid, File helperFile, String filename) throws IOException;

    void uploadGradingHelperFileZipped(String userJid, String problemJid, File helperFileZipped) throws IOException;

    List<FileInfo> getGradingTestDataFiles(String userJid, String problemJid);

    List<FileInfo> getGradingHelperFiles(String userJid, String problemJid);

    String getGradingTestDataFileURL(String userJid, String problemJid, String filename);

    String getGradingHelperFileURL(String userJid, String problemJid, String filename);

    ByteArrayOutputStream getZippedGradingFilesStream(String problemJid) throws IOException;
}
