import { useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useMemo } from 'react';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget from '../../../../../../components/LanguageWidget/StatementLanguageWidget';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { consolidateLanguages } from '../../../../../../modules/api/sandalphon/language';
import { getProblemName } from '../../../../../../modules/api/sandalphon/problem';
import { ContestProblemStatus } from '../../../../../../modules/api/uriel/contestProblem';
import { contestBySlugQueryOptions } from '../../../../../../modules/queries/contest';
import { contestProblemsQueryOptions } from '../../../../../../modules/queries/contestProblem';
import { useWebPrefs } from '../../../../../../modules/webPrefs';
import { ContestProblemCard } from '../ContestProblemCard/ContestProblemCard';
import { ContestProblemEditDialog } from '../ContestProblemEditDialog/ContestProblemEditDialog';

export default function ContestProblemsPage() {
  const { contestSlug } = useParams({ strict: false });
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));
  const { statementLanguage } = useWebPrefs();

  const { data: response } = useQuery(contestProblemsQueryOptions(contest.jid));

  const { defaultLanguage, uniqueLanguages } = useMemo(() => {
    if (!response) {
      return {};
    }
    return consolidateLanguages(response.problemsMap, statementLanguage);
  }, [response, statementLanguage]);

  const renderSetDialog = () => {
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
    return <ContestProblemEditDialog contest={contest} problems={problems} />;
  };

  const renderStatementLanguageWidget = () => {
    if (!defaultLanguage || !uniqueLanguages) {
      return null;
    }

    return <StatementLanguageWidget defaultLanguage={defaultLanguage} statementLanguages={uniqueLanguages} />;
  };

  const renderProblems = () => {
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
        problemName: getProblemName(response.problemsMap[problem.problemJid], defaultLanguage),
        totalSubmissions: totalSubmissionsMap[problem.problemJid],
      };
      return <ContestProblemCard key={problem.problemJid} {...props} />;
    });
  };

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
}
