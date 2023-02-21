package judgels.michael.template;

public abstract class HtmlForm {
    private String globalError = "";

    public String getGlobalError() {
        return globalError;
    }

    public HtmlForm withGlobalError(String globalError) {
        this.globalError = globalError;
        return this;
    }
}
