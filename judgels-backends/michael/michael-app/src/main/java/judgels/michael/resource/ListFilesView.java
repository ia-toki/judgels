package judgels.michael.resource;

import java.util.List;
import judgels.fs.FileInfo;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;

public class ListFilesView extends TemplateView {
    private final String currentPath;
    private final List<FileInfo> files;

    public ListFilesView(HtmlTemplate template, String currentPath, List<FileInfo> files) {
        super("listFilesView.ftl", template);
        this.currentPath = currentPath;
        this.files = files;
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public List<FileInfo> getFiles() {
        return files;
    }
}
