package org.iatoki.judgels.play;

import java.util.List;

public final class Page<T> {

    private List<T> data;
    private final long pageSize;
    private final long totalRowsCount;
    private final long pageIndex;

    public Page(List<T> data, long totalRowsCount, long pageIndex, long pageSize) {
        this.data = data;
        this.totalRowsCount = totalRowsCount;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public long getTotalRowsCount() {
        return totalRowsCount;
    }

    public long getPageIndex() {
        return pageIndex;
    }

    public long getPageSize() {
        return pageSize;
    }

    public long getTotalPagesCount() {
        return (totalRowsCount + pageSize - 1) / pageSize;
    }

    public List<T> getData() {
        return data;
    }

    public boolean hasPreviousPage() {
        return pageIndex > 0;
    }

    public boolean hasNextPage() {
        return pageIndex + 1 < getTotalPagesCount();
    }

    public long getCurrentFirstRowIndex() {
        return pageIndex * pageSize;
    }

    public long getCurrentLastRowIndex() {
        return getCurrentFirstRowIndex() + data.size() - 1;
    }
}
