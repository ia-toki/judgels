package judgels.catalog;

import com.google.common.collect.Lists;
import java.util.List;
import judgels.api.catalog.CatalogVersion;
import judgels.core.git.GitCommit;

public class CatalogVersions {
    private CatalogVersions() {}

    public static List<CatalogVersion> fromGitCommits(List<GitCommit> commits) {
        return Lists.transform(commits, c -> new CatalogVersion.Builder()
                .hash(c.getHash())
                .userJid(c.getUserJid())
                .time(c.getTime().toInstant())
                .title(c.getTitle())
                .description(getDescription(c))
                .build());
    }

    // A commit's description is its whole message, which starts with its title.
    private static String getDescription(GitCommit commit) {
        String description = commit.getDescription();
        if (description.startsWith(commit.getTitle())) {
            description = description.substring(commit.getTitle().length());
        }
        return description.strip();
    }
}
