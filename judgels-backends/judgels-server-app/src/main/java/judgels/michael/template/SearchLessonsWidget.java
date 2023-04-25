package judgels.michael.template;

public class SearchLessonsWidget {
    private final int pageNumber;
    private final String termFilter;

    public SearchLessonsWidget(int pageNumber, String termFilter) {
        this.pageNumber = pageNumber;
        this.termFilter = termFilter;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public String getTermFilter() {
        return termFilter;
    }
}
