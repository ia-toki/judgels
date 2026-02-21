import { useMutation, useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useEffect } from 'react';

import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget from '../../../../../../../../components/LanguageWidget/StatementLanguageWidget';
import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import { ProblemWorksheetCard } from '../../../../../../../../components/ProblemWorksheetCard/Bundle/ProblemWorksheetCard';
import { contestBySlugQueryOptions } from '../../../../../../../../modules/queries/contest';
import { contestBundleProblemWorksheetQueryOptions } from '../../../../../../../../modules/queries/contestProblem';
import {
  contestBundleLatestSubmissionsQueryOptions,
  createBundleItemSubmissionMutationOptions,
} from '../../../../../../../../modules/queries/contestSubmissionBundle';
import { useWebPrefs } from '../../../../../../../../modules/webPrefs';
import { createDocumentTitle } from '../../../../../../../../utils/title';

export default function ContestProblemPage() {
  const { contestSlug, problemAlias } = useParams({ strict: false });
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));
  const { statementLanguage } = useWebPrefs();

  const { data: response } = useQuery(
    contestBundleProblemWorksheetQueryOptions(contest.jid, problemAlias, { language: statementLanguage })
  );

  const { data: latestSubmissions } = useQuery({
    ...contestBundleLatestSubmissionsQueryOptions(contest.jid, response?.problem?.alias),
    enabled: !!response,
  });

  const createSubmissionMutation = useMutation(createBundleItemSubmissionMutationOptions(contest.jid, problemAlias));

  useEffect(() => {
    if (response) {
      document.title = createDocumentTitle(`Problem ${response.problem.alias}`);
    }
  }, [response?.problem?.alias]);

  const onCreateSubmission = async (itemJid, answer) => {
    await createSubmissionMutation.mutateAsync({
      problemJid: response.problem.problemJid,
      itemJid,
      answer,
    });
  };

  const renderStatementLanguageWidget = () => {
    if (!response) {
      return null;
    }
    return (
      <div className="language-widget-wrapper">
        <StatementLanguageWidget defaultLanguage={response.defaultLanguage} statementLanguages={response.languages} />
      </div>
    );
  };

  const renderStatement = () => {
    if (!response) {
      return <LoadingState />;
    }

    if (!latestSubmissions) {
      return <LoadingState />;
    }

    return (
      <ProblemWorksheetCard
        alias={response.problem.alias}
        latestSubmissions={latestSubmissions}
        onAnswerItem={onCreateSubmission}
        worksheet={response.worksheet}
      />
    );
  };

  return (
    <ContentCard>
      {renderStatementLanguageWidget()}
      {renderStatement()}
    </ContentCard>
  );
}
