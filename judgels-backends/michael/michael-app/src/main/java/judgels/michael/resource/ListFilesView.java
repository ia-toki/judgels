package judgels.michael.resource;

import java.util.List;
import judgels.fs.FileInfo;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;

public class ListFilesView extends TemplateView {
    private final String currentUrl;
    private final List<FileInfo> files;

    public ListFilesView(HtmlTemplate template, String currentUrl, List<FileInfo> files) {
        super("listFilesView.ftl", template);
        this.currentUrl = currentUrl;
        this.files = files;
    }

    public String getCurrentUrl() {
        return currentUrl;
    }

    public List<FileInfo> getFiles() {
        return files;
    }
}
