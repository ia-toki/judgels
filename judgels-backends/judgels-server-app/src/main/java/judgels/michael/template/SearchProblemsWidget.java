package judgels.michael.template;

import java.util.List;
import java.util.Map;
import judgels.sandalphon.problem.base.tag.ProblemTags;

public class SearchProblemsWidget {
    private final int pageIndex;
    private final String filterString;
    private final List<String> tags;
    private final Map<String, Integer> tagCounts;

    public SearchProblemsWidget(int pageIndex, String filterString, List<String> tags, Map<String, Integer> tagCounts) {
        this.pageIndex = pageIndex;
        this.filterString = filterString;
        this.tags = tags;
        this.tagCounts = tagCounts;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public String getFilterString() {
        return filterString;
    }

    public List<String> getTags() {
        return tags;
    }

    public Map<String, Integer> getTagCounts() {
        return tagCounts;
    }

    public List<String> getTopicTags() {
        return ProblemTags.TOPIC_TAGS;
    }
}
