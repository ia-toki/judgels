package judgels.sandalphon.problem.base.tag;

import static java.util.stream.Collectors.toSet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProblemTags {
    private ProblemTags() {}

    public static final List<Set<String>> DERIVED_TAG_GROUPS = ImmutableList.of(
            ImmutableSet.of("visibility-private", "visibility-public"),
            ImmutableSet.of("statement-en"),
            ImmutableSet.of("editorial-no", "editorial-yes", "editorial-en"),
            ImmutableSet.of("engine-batch", "engine-interactive", "engine-output-only", "engine-functional"),
            ImmutableSet.of("scoring-partial", "scoring-subtasks", "scoring-absolute"));

    public static final List<String> TOPIC_TAGS = ImmutableList.of(
            "ad hoc",
            "bitwise operation",
            "constructive",
            "data structure",
            "data structure: binary search tree",
            "data structure: bitset",
            "data structure: compression",
            "data structure: disjoint set union",
            "data structure: fenwick tree",
            "data structure: heap",
            "data structure: monotonic",
            "data structure: persistent",
            "data structure: prefix sum",
            "data structure: segment tree",
            "data structure: sparse table",
            "data structure: sqrt decomposition",
            "data structure: trie",
            "dynamic programming",
            "dynamic programming: bitmask",
            "dynamic programming: broken profile",
            "dynamic programming: combinatorics",
            "dynamic programming: connected components",
            "dynamic programming: digit",
            "dynamic programming: matrix exponentiation",
            "dynamic programming: open-close interval",
            "dynamic programming: sum over subsets",
            "dynamic programming: tree",
            "dynamic programming: x2+1",
            "game theory",
            "game theory: sprague-grundy number",
            "geometry",
            "geometry: convex hull",
            "geometry: delaunay triangulation",
            "graph",
            "graph: 2-satisfiability",
            "graph: biconnected components",
            "graph: bipartite",
            "graph: eulerian path",
            "graph: flood fill",
            "graph: hall's marriage theorem",
            "graph: max flow",
            "graph: max matching",
            "graph: min cost flow",
            "graph: min spanning tree",
            "graph: planar",
            "graph: reachability tree",
            "graph: shortest path",
            "graph: strongly connected components",
            "graph: topological sort",
            "graph: traversal",
            "greedy",
            "greedy: matroid",
            "heuristic",
            "heuristic: las vegas",
            "information theory",
            "implementation",
            "math",
            "math: big integer",
            "math: burnside's lemma",
            "math: chinese remainder theorem",
            "math: combinatorics",
            "math: fast fourier transform",
            "math: gaussian elimination",
            "math: inclusion-exclusion",
            "math: inversion",
            "math: linearity of expectation",
            "math: number theory",
            "math: pigeonhole principle",
            "math: polynomials",
            "math: probability",
            "optimization trick",
            "optimization trick: convex hull",
            "optimization trick: divide-and-conquer",
            "optimization trick: knuth",
            "optimization trick: lagrangian relaxation",
            "optimization trick: slope",
            "searching",
            "searching: binary search",
            "searching: brute force",
            "searching: divide-and-conquer",
            "searching: meet-in-the-middle",
            "searching: sweep line",
            "searching: ternary search",
            "searching: two pointers",
            "string",
            "string: aho-corasick",
            "string: hashing",
            "string: knuth-morris-pratt",
            "string: suffix structure",
            "string: z-function",
            "tree",
            "tree: centroid decomposition",
            "tree: diameter",
            "tree: flattening",
            "tree: heavy-light decomposition",
            "tree: lowest common ancestor",
            "tree: small-to-large",
            "EOF")
            .stream()
            .filter(s -> !s.equals("EOF"))
            .map(s -> "topic-" + s)
            .collect(Collectors.toList());

    public static boolean isChildTag(String tag, String childTag) {
        return !tag.equals(childTag) && childTag.startsWith(tag);
    }

    public static List<Set<String>> splitTagsFilterByType(Set<String> tags) {
        List<Set<String>> result = new ArrayList<>();
        for (Set<String> tagGroup : DERIVED_TAG_GROUPS) {
            result.add(Sets.intersection(tags, tagGroup));
        }

        result.add(tags.stream()
                .filter(tag -> tag.startsWith("topic-"))
                .filter(tag -> tags.stream().noneMatch(t -> isChildTag(tag, t)))
                .collect(toSet()));

        return Collections.unmodifiableList(result);
    }
}
