package org.iatoki.judgels.play.template;

import play.api.mvc.Call;

public final class InternalLink {

    private String label;
    private Call target;

    public InternalLink(String label, Call target) {
        this.label = label;
        this.target = target;
    }

    public String getLabel() {
        return label;
    }

    public Call getTarget() {
        return target;
    }
}
