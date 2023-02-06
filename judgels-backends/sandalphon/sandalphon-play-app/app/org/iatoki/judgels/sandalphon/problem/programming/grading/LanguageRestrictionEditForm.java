package org.iatoki.judgels.sandalphon.problem.programming.grading;

import java.util.Map;

public class LanguageRestrictionEditForm {

    public Map<String, String> allowedLanguageNames;

    public boolean isAllowedAll;

    public Map<String, String> getAllowedLanguageNames() {
        return allowedLanguageNames;
    }

    public void setAllowedLanguageNames(Map<String, String> allowedLanguageNames) {
        this.allowedLanguageNames = allowedLanguageNames;
    }

    public boolean getIsAllowedAll() {
        return isAllowedAll;
    }

    public void setIsAllowedAll(boolean allowedAll) {
        isAllowedAll = allowedAll;
    }
}
