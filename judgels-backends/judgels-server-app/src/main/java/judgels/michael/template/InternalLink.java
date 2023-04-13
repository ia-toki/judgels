package judgels.michael.template;

public class InternalLink {
    private final String key;
    private final String label;
    private final String target;

    public InternalLink(String key, String label, String target) {
        this.key = key;
        this.label = label;
        this.target = target;
    }

    public InternalLink(String label, String target) {
        this("", label, target);
    }

    public String getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public String getTarget() {
        return target;
    }
}
