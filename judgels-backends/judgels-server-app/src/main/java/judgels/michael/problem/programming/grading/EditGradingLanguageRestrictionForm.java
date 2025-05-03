package judgels.michael.problem.programming.grading;

import jakarta.ws.rs.FormParam;
import java.util.HashSet;
import java.util.Set;
import judgels.michael.template.HtmlForm;

public class EditGradingLanguageRestrictionForm extends HtmlForm {
    @FormParam("isAllowedAll")
    boolean isAllowedAll;

    @FormParam("allowedLanguages")
    Set<String> allowedLanguages = new HashSet<>();

    public boolean getIsAllowedAll() {
        return isAllowedAll;
    }

    public Set<String> getAllowedLanguages() {
        return allowedLanguages;
    }
}
