import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget from '../../../../../../components/LanguageWidget/StatementLanguageWidget';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { consolidateLanguages } from '../../../../../../modules/api/sandalphon/language';
import { getProblemName } from '../../../../../../modules/api/sandalphon/problem';
import { ContestProblemStatus } from '../../../../../../modules/api/uriel/contestProblem';
import { contestBySlugQueryOptions } from '../../../../../../modules/queries/contest';
import { selectToken } from '../../../../../../modules/session/sessionSelectors';
import { useWebPrefs } from '../../../../../../modules/webPrefs';
import { ContestProblemCard } from '../ContestProblemCard/ContestProblemCard';
import { ContestProblemEditDialog } from '../ContestProblemEditDialog/ContestProblemEditDialog';

import * as contestProblemActions from '../modules/contestProblemActions';

export default function ContestProblemsPage() {
  const { contestSlug } = useParams({ strict: false });
  const token = useSelector(selectToken);
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(token, contestSlug));
  const dispatch = useDispatch();
  const { statementLanguage } = useWebPrefs();

  const [state, setState] = useState({
    response: undefined,
    defaultLanguage: undefined,
    uniqueLanguages: undefined,
  });

  const refreshProblems = async () => {
    const response = await dispatch(contestProblemActions.getProblems(contest.jid));
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
        <div className="content-card__header">
          {renderSetDialog()}
          {renderStatementLanguageWidget()}
          <div className="clearfix" />
        </div>
        {renderProblems()}
      </ContentCard>
    );
  };

  const renderSetDialog = () => {
    const { response } = state;
    if (!response || !response.config.canManage) {
      return null;
    }

    const problems = response.data.map(p => ({
      alias: p.alias,
      slug: response.problemsMap[p.problemJid].slug,
      status: p.status,
      submissionsLimit: p.submissionsLimit,
      points: p.points,
    }));
    return <ContestProblemEditDialog contest={contest} problems={problems} onSetProblems={setProblems} />;
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
      return <LoadingState />;
    }

    const { data: problems, totalSubmissionsMap } = response;

    if (problems.length === 0) {
      return (
        <p>
          <small>No problems.</small>
        </p>
      );
    }

    return (
      <div>
        {renderOpenProblems(
          problems.filter(p => p.status === ContestProblemStatus.Open),
          totalSubmissionsMap
        )}
        {renderClosedProblems(
          problems.filter(p => p.status === ContestProblemStatus.Closed),
          totalSubmissionsMap
        )}
      </div>
    );
  };

  const renderOpenProblems = (problems, totalSubmissionsMap) => {
    return <div>{renderFilteredProblems(problems, totalSubmissionsMap)}</div>;
  };

  const renderClosedProblems = (problems, totalSubmissionsMap) => {
    return (
      <div>
        {problems.length !== 0 && <hr />}
        {renderFilteredProblems(problems, totalSubmissionsMap)}
      </div>
    );
  };

  const renderFilteredProblems = (problems, totalSubmissionsMap) => {
    return problems.map(problem => {
      const props = {
        contest,
        problem,
        problemName: getProblemName(state.response.problemsMap[problem.problemJid], state.defaultLanguage),
        totalSubmissions: totalSubmissionsMap[problem.problemJid],
      };
      return <ContestProblemCard key={problem.problemJid} {...props} />;
    });
  };

  const setProblems = async (contestJid, data) => {
    const response = await dispatch(contestProblemActions.setProblems(contestJid, data));
    await refreshProblems();
    return response;
  };

  return render();
}
