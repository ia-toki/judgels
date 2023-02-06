package org.iatoki.judgels.sandalphon.problem.base.editorial;

import play.data.validation.Constraints;

public class EditorialCreateForm {
    @Constraints.Required
    public String initLanguageCode;

    public String getInitLanguageCode() {
        return initLanguageCode;
    }

    public void setInitLanguageCode(String initLanguageCode) {
        this.initLanguageCode = initLanguageCode;
    }
}
