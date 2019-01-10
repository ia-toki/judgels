package org.iatoki.judgels.play;

import play.api.mvc.Call;

/**
 * @deprecated Has been restructured to different package.
 */
@Deprecated
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
