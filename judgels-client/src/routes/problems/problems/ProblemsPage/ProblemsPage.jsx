import { parse } from 'query-string';
import { useEffect, useState } from 'react';
import { useDispatch } from 'react-redux';
import { useLocation } from 'react-router';

import { Card } from '../../../../components/Card/Card';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../components/Pagination/Pagination';
import { ProblemSetProblemCard } from '../../../../components/ProblemSetProblemCard/ProblemSetProblemCard';
import ProblemSpoilerWidget from '../../../../components/ProblemSpoilerWidget/ProblemSpoilerWidget';
import { ProblemType, getProblemName } from '../../../../modules/api/sandalphon/problem';

import * as problemActions from '../modules/problemActions';

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
  const dispatch = useDispatch();

  const queries = parse(location.search);
  const tags = parseTags(queries.tags);

  const [state, setState] = useState({
    response: undefined,
  });

  const render = () => {
    return (
      <Card title="Browse problems">
        {renderHeader()}
        <hr />
        {renderProblems()}
        {renderPagination()}
      </Card>
    );
  };

  const renderHeader = () => {
    return <ProblemSpoilerWidget />;
  };

  const renderProblems = () => {
    const { response } = state;
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

  const renderPagination = () => {
    return <Pagination pageSize={PAGE_SIZE} onChangePage={onChangePage} key={JSON.stringify(tags)} />;
  };

  const onChangePage = async nextPage => {
    if (state.response) {
      setState({ response: { ...state.response, data: undefined } });
    }
    const data = await refreshProblems(nextPage);
    return data.totalCount;
  };

  const refreshProblems = async page => {
    const response = await dispatch(problemActions.getProblems(tags, page));
    setState({ response });
    return response.data;
  };

  return render();
}
