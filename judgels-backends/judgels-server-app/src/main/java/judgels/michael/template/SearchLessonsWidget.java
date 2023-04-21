package judgels.michael.template;

public class SearchLessonsWidget {
    private final int pageIndex;
    private final String termFilter;

    public SearchLessonsWidget(int pageIndex, String termFilter) {
        this.pageIndex = pageIndex;
        this.termFilter = termFilter;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public String getTermFilter() {
        return termFilter;
    }
}
