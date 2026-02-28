import { useQuery } from '@tanstack/react-query';
import { useLocation } from '@tanstack/react-router';

import { Card } from '../../../../components/Card/Card';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../components/Pagination/Pagination';
import { ProblemSetProblemCard } from '../../../../components/ProblemSetProblemCard/ProblemSetProblemCard';
import ProblemSpoilerWidget from '../../../../components/ProblemSpoilerWidget/ProblemSpoilerWidget';
import { ProblemType, getProblemName } from '../../../../modules/api/sandalphon/problem';
import { problemsQueryOptions } from '../../../../modules/queries/problem';

const PAGE_SIZE = 20;

const parseTags = queryTags => {
  let tags = queryTags || [];
  if (typeof tags === 'string') {
    tags = [tags];
  }
  return tags;
};

export default function ProblemsPage() {
  const location = useLocation();

  const tags = parseTags(location.search.tags);
  const page = +(location.search.page || 1);

  const { data: response } = useQuery(problemsQueryOptions({ tags, page }));

  const renderProblems = () => {
    if (!response || !response.data) {
      return <LoadingState />;
    }

    const { data: problems, problemsMap, problemMetadatasMap, problemDifficultiesMap, problemProgressesMap } = response;

    if (problems.page.length === 0) {
      if (tags.length === 0) {
        return (
          <>
            <p>To view problems, select some filters on the left.</p>
            <p>We will refine this page in the future.</p>
          </>
        );
      }

      return (
        <p>
          <small>No problems found.</small>
        </p>
      );
    }

    return problems.page.map(problem => {
      const { problemSetSlug, problemAlias, problemJid } = problem;
      const props = {
        problemSet: { slug: problemSetSlug },
        problem: { type: ProblemType.Programming, alias: problemAlias },
        problemName: getProblemName(problemsMap[problemJid], 'en'),
        metadata: problemMetadatasMap[problemJid],
        difficulty: problemDifficultiesMap[problemJid],
        progress: problemProgressesMap[problemJid],
      };
      return <ProblemSetProblemCard key={problemJid} {...props} />;
    });
  };

  return (
    <Card title="Browse problems">
      <ProblemSpoilerWidget />
      <hr />
      {renderProblems()}
      {response && <Pagination pageSize={PAGE_SIZE} totalCount={response.data.totalCount} />}
    </Card>
  );
}
