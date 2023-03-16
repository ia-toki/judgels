package judgels.michael.resource;

import java.util.List;
import judgels.fs.FileInfo;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;

public class ListFilesView extends TemplateView {
    private final String currentPath;
    private final List<FileInfo> files;
    private final boolean canEdit;

    public ListFilesView(HtmlTemplate template, String currentPath, List<FileInfo> files, boolean canEdit) {
        super("listFilesView.ftl", template);
        this.currentPath = currentPath;
        this.files = files;
        this.canEdit = canEdit;
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public List<FileInfo> getFiles() {
        return files;
    }

    public boolean getCanEdit() {
        return canEdit;
    }
}
