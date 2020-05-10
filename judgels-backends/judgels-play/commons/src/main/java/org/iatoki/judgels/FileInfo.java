package org.iatoki.judgels;

import java.util.Date;

public final class FileInfo {

    private final String name;
    private final long size;
    private final Date lastModifiedTime;

    public FileInfo(String name, long size, Date lastModifiedTime) {
        this.name = name;
        this.size = size;
        this.lastModifiedTime = lastModifiedTime;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public Date getLastModifiedTime() {
        return lastModifiedTime;
    }
}
