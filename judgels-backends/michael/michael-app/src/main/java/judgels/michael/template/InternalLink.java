package judgels.michael.template;

public class InternalLink {
    private final String label;
    private final String target;

    public InternalLink(String label, String target) {
        this.label = label;
        this.target = target;
    }

    public String getLabel() {
        return label;
    }

    public String getTarget() {
        return target;
    }
}
