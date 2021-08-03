package org.iatoki.judgels.sandalphon.problem.base.tag;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.stream.Collectors;

public class ProblemTags {
    private ProblemTags() {}

    public static final List<String> TOPIC_TAGS = ImmutableList.of(
            "ad hoc",
            "bits",
            "bits: bitset",
            "bits: bitwise operation",
            "bits: gray code",
            "constructive",
            "data structure",
            "data structure: binary search tree",
            "data structure: deque",
            "data structure: disjoint set union",
            "data structure: fenwick tree",
            "data structure: heap",
            "data structure: linked list",
            "data structure: persistent",
            "data structure: range min/max query",
            "data structure: segment tree",
            "data structure: sparse table",
            "data structure: sqrt decomposition",
            "data structure: stack",
            "data structure: trie",
            "dynamic programming",
            "dynamic programming: bitmask",
            "dynamic programming: broken profile",
            "dynamic programming: combinatorics",
            "dynamic programming: connected components",
            "dynamic programming: matrix exponentiation",
            "dynamic programming: sum over subsets",
            "game theory",
            "game theory: sprague-grundy number",
            "geometry",
            "geometry: convex hull",
            "geometry: delaunay triangulation",
            "geometry: sweep line",
            "graph",
            "graph: 2-satisfiability",
            "graph: biconnected components",
            "graph: eulerian path",
            "graph: flood fill",
            "graph: hall's marriage theorem",
            "graph: max flow",
            "graph: max matching",
            "graph: min cost flow",
            "graph: min spanning tree",
            "graph: reachability tree",
            "graph: shortest path",
            "graph: strongly connected components",
            "graph: topological sort",
            "graph: traversal",
            "greedy",
            "greedy: matroid",
            "greedy: sorting",
            "heuristic",
            "heuristic: las vegas",
            "information theory",
            "implementation",
            "math",
            "math: big integer",
            "math: burnside's lemma",
            "math: combinatorics",
            "math: fast fourier transform",
            "math: gaussian elimination",
            "math: greatest common divisor",
            "math: inclusion-exclusion",
            "math: inversion",
            "math: linearity of expectation",
            "math: number base",
            "math: number theory",
            "math: permutation",
            "math: pigeonhole principle",
            "math: polynomials",
            "math: prime number",
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
            "tree: heavy-light decomposition",
            "tree: lowest common ancestor",
            "tree: tree flattening",
            "EOF")
            .stream()
            .filter(s -> !s.equals("EOF"))
            .map(s -> "topic-" + s)
            .collect(Collectors.toList());
}
