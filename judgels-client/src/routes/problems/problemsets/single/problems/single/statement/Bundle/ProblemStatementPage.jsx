import { useMutation, useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useLocation, useParams } from '@tanstack/react-router';

import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget from '../../../../../../../../components/LanguageWidget/StatementLanguageWidget';
import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import { ProblemWorksheetCard } from '../../../../../../../../components/ProblemWorksheetCard/Bundle/ProblemWorksheetCard';
import { problemSetBySlugQueryOptions } from '../../../../../../../../modules/queries/problemSet';
import {
  createProblemSetBundleItemSubmissionMutationOptions,
  problemSetBundleLatestSubmissionsQueryOptions,
} from '../../../../../../../../modules/queries/problemSetSubmissionBundle';

export default function ProblemStatementPage(props) {
  const { problemSetSlug } = useParams({ strict: false });
  const location = useLocation();
  const { data: problemSet } = useSuspenseQuery(problemSetBySlugQueryOptions(problemSetSlug));

  const { data: latestSubmissions } = useQuery(
    problemSetBundleLatestSubmissionsQueryOptions(problemSet.jid, props.worksheet.problem.alias)
  );

  const createItemSubmissionMutation = useMutation(
    createProblemSetBundleItemSubmissionMutationOptions(problemSet.jid, props.worksheet.problem.alias)
  );

  const createSubmission = async (itemJid, answer) => {
    const { problem } = props.worksheet;
    await createItemSubmissionMutation.mutateAsync({ problemJid: problem.problemJid, itemJid, answer });
  };

  const renderStatementLanguageWidget = () => {
    const { defaultLanguage, languages } = props.worksheet;
    if (!defaultLanguage || !languages) {
      return null;
    }
    const widgetProps = {
      defaultLanguage: defaultLanguage,
      statementLanguages: languages,
    };
    return (
      <div className="language-widget-wrapper">
        <StatementLanguageWidget {...widgetProps} />
      </div>
    );
  };

  const renderStatement = () => {
    const { problem, worksheet } = props.worksheet;
    if (!problem || !worksheet) {
      return <LoadingState />;
    }

    if (!latestSubmissions) {
      return <LoadingState />;
    }
    const resultsUrl = (location.pathname + '/results').replace('//', '/');

    return (
      <ProblemWorksheetCard
        latestSubmissions={latestSubmissions}
        onAnswerItem={createSubmission}
        worksheet={worksheet}
        resultsUrl={resultsUrl}
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
