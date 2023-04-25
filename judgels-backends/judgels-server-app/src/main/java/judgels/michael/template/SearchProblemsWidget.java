package judgels.michael.template;

import java.util.List;
import java.util.Map;
import java.util.Set;
import judgels.sandalphon.problem.base.tag.ProblemTags;

public class SearchProblemsWidget {
    private final int pageNumber;
    private final String termFilter;
    private final Set<String> tagsFilter;
    private final Map<String, Integer> tagCounts;

    public SearchProblemsWidget(int pageNumber, String termFilter, Set<String> tagsFilter, Map<String, Integer> tagCounts) {
        this.pageNumber = pageNumber;
        this.termFilter = termFilter;
        this.tagsFilter = tagsFilter;
        this.tagCounts = tagCounts;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public String getTermFilter() {
        return termFilter;
    }

    public Set<String> getTagsFilter() {
        return tagsFilter;
    }

    public Map<String, Integer> getTagCounts() {
        return tagCounts;
    }

    public List<String> getTopicTags() {
        return ProblemTags.TOPIC_TAGS;
    }
}
