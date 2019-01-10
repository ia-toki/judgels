package org.iatoki.judgels.jophiel.viewpoint;

import play.data.validation.Constraints;

public final class ViewpointForm {

    @Constraints.Required
    public String username;
}
