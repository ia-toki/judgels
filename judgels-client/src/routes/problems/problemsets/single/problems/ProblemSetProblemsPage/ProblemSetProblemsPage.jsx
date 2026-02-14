import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget from '../../../../../../components/LanguageWidget/StatementLanguageWidget';
import { LoadingContentCard } from '../../../../../../components/LoadingContentCard/LoadingContentCard';
import { ProblemSetProblemCard } from '../../../../../../components/ProblemSetProblemCard/ProblemSetProblemCard';
import ProblemSpoilerWidget from '../../../../../../components/ProblemSpoilerWidget/ProblemSpoilerWidget';
import { consolidateLanguages } from '../../../../../../modules/api/sandalphon/language';
import { getProblemName } from '../../../../../../modules/api/sandalphon/problem';
import { problemSetBySlugQueryOptions } from '../../../../../../modules/queries/problemSet';
import { selectStatementLanguage } from '../../../../../../modules/webPrefs/webPrefsSelectors';

import * as problemSetProblemActions from '../modules/problemSetProblemActions';

export default function ProblemSetProblemsPage() {
  const { problemSetSlug } = useParams({ strict: false });
  const { data: problemSet } = useSuspenseQuery(problemSetBySlugQueryOptions(problemSetSlug));
  const statementLanguage = useSelector(selectStatementLanguage);
  const dispatch = useDispatch();

  const [state, setState] = useState({
    response: undefined,
    defaultLanguage: undefined,
    uniqueLanguages: undefined,
  });

  const refreshProblems = async () => {
    const response = await dispatch(problemSetProblemActions.getProblems(problemSet.jid));
    const { defaultLanguage, uniqueLanguages } = consolidateLanguages(response.problemsMap, statementLanguage);

    setState({
      response,
      defaultLanguage,
      uniqueLanguages,
    });
  };

  useEffect(() => {
    refreshProblems();
  }, [statementLanguage]);

  const render = () => {
    return (
      <ContentCard>
        <h3>Problems</h3>
        <hr />
        {renderHeader()}
        {renderProblems()}
      </ContentCard>
    );
  };

  const renderHeader = () => {
    return (
      <>
        <div className="float-left">{renderProblemSpoilerWidget()}</div>
        <div className="float-right">{renderStatementLanguageWidget()}</div>
        <div className="clearfix" />
      </>
    );
  };

  const renderProblemSpoilerWidget = () => {
    return <ProblemSpoilerWidget />;
  };

  const renderStatementLanguageWidget = () => {
    const { defaultLanguage, uniqueLanguages } = state;
    if (!defaultLanguage || !uniqueLanguages) {
      return null;
    }

    const props = {
      defaultLanguage,
      statementLanguages: uniqueLanguages,
    };
    return <StatementLanguageWidget {...props} />;
  };

  const renderProblems = () => {
    const { response } = state;
    if (!response) {
      return <LoadingContentCard />;
    }

    const { data: problems, problemsMap, problemMetadatasMap, problemDifficultiesMap, problemProgressesMap } = response;

    if (problems.length === 0) {
      return (
        <p>
          <small>No problems.</small>
        </p>
      );
    }

    return problems.map(problem => {
      const props = {
        problemSet,
        problem,
        showAlias: true,
        problemName: getProblemName(problemsMap[problem.problemJid], state.defaultLanguage),
        metadata: problemMetadatasMap[problem.problemJid],
        difficulty: problemDifficultiesMap[problem.problemJid],
        progress: problemProgressesMap[problem.problemJid],
      };
      return <ProblemSetProblemCard key={problem.problemJid} {...props} />;
    });
  };

  return render();
}
