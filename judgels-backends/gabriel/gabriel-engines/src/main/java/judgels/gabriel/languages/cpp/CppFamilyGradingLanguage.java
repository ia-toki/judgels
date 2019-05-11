package judgels.gabriel.languages.cpp;

import java.util.List;
import judgels.gabriel.api.GradingLanguage;

public interface CppFamilyGradingLanguage extends GradingLanguage {
    List<String> getCompilationOnlyCommand(String sourceFilename, String objectFilename);
}
