package org.iatoki.judgels.sandalphon.resource;

import java.io.File;

public final class UploadFileForm {

    public File file;

    public File fileZipped;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFileZipped() {
        return fileZipped;
    }

    public void setFileZipped(File fileZipped) {
        this.fileZipped = fileZipped;
    }
}
