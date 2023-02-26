package judgels.michael.template;

public class SearchProblemsWidget {
    private final int pageIndex;
    private final String filterString;

    public SearchProblemsWidget(int pageIndex, String filterString) {
        this.pageIndex = pageIndex;
        this.filterString = filterString;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public String getFilterString() {
        return filterString;
    }
}
