import { useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget from '../../../../../../components/LanguageWidget/StatementLanguageWidget';
import { LoadingContentCard } from '../../../../../../components/LoadingContentCard/LoadingContentCard';
import { ProblemSetProblemCard } from '../../../../../../components/ProblemSetProblemCard/ProblemSetProblemCard';
import ProblemSpoilerWidget from '../../../../../../components/ProblemSpoilerWidget/ProblemSpoilerWidget';
import { consolidateLanguages } from '../../../../../../modules/api/sandalphon/language';
import { getProblemName } from '../../../../../../modules/api/sandalphon/problem';
import {
  problemSetBySlugQueryOptions,
  problemSetProblemsQueryOptions,
} from '../../../../../../modules/queries/problemSet';
import { useWebPrefs } from '../../../../../../modules/webPrefs';

export default function ProblemSetProblemsPage() {
  const { problemSetSlug } = useParams({ strict: false });
  const { data: problemSet } = useSuspenseQuery(problemSetBySlugQueryOptions(problemSetSlug));
  const { statementLanguage } = useWebPrefs();

  const { data: response } = useQuery(problemSetProblemsQueryOptions(problemSet.jid));

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
    if (!response) {
      return null;
    }
    const { defaultLanguage, uniqueLanguages } = consolidateLanguages(response.problemsMap, statementLanguage);
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
    if (!response) {
      return <LoadingContentCard />;
    }

    const { data: problems, problemsMap, problemMetadatasMap, problemDifficultiesMap, problemProgressesMap } = response;
    const { defaultLanguage } = consolidateLanguages(problemsMap, statementLanguage);

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
        problemName: getProblemName(problemsMap[problem.problemJid], defaultLanguage),
        metadata: problemMetadatasMap[problem.problemJid],
        difficulty: problemDifficultiesMap[problem.problemJid],
        progress: problemProgressesMap[problem.problemJid],
      };
      return <ProblemSetProblemCard key={problem.problemJid} {...props} />;
    });
  };

  return (
    <ContentCard>
      <h3>Problems</h3>
      <hr />
      {renderHeader()}
      {renderProblems()}
    </ContentCard>
  );
}
