import { useMutation, useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useNavigate, useParams } from '@tanstack/react-router';
import { useEffect } from 'react';

import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget from '../../../../../../../../components/LanguageWidget/StatementLanguageWidget';
import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import { ProblemWorksheetCard } from '../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemWorksheetCard';
import { contestBySlugQueryOptions } from '../../../../../../../../modules/queries/contest';
import { contestProgrammingProblemWorksheetQueryOptions } from '../../../../../../../../modules/queries/contestProblem';
import { createProgrammingSubmissionMutationOptions } from '../../../../../../../../modules/queries/contestSubmissionProgramming';
import { useWebPrefs } from '../../../../../../../../modules/webPrefs';
import { createDocumentTitle } from '../../../../../../../../utils/title';

import * as toastActions from '../../../../../../../../modules/toast/toastActions';

import './ContestProblemPage.scss';

export default function ContestProblemPage() {
  const { contestSlug, problemAlias } = useParams({ strict: false });
  const navigate = useNavigate();
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));
  const { statementLanguage, gradingLanguage, setGradingLanguage } = useWebPrefs();

  const { data: response } = useQuery(
    contestProgrammingProblemWorksheetQueryOptions(contest.jid, problemAlias, { language: statementLanguage })
  );

  const createSubmissionMutation = useMutation(
    createProgrammingSubmissionMutationOptions(contest.jid, response?.problem?.problemJid)
  );

  useEffect(() => {
    if (response) {
      document.title = createDocumentTitle(`Problem ${response.problem.alias}`);
    }
  }, [response?.problem?.alias]);

  const createSubmission = async data => {
    setGradingLanguage(data.gradingLanguage);
    await createSubmissionMutation.mutateAsync(data, {
      onSuccess: () => {
        toastActions.showSuccessToast('Solution submitted.');
        window.scrollTo(0, 0);
        navigate({ to: `/contests/${contestSlug}/submissions` });
      },
    });
  };

  const renderStatementLanguageWidget = () => {
    if (!response) {
      return null;
    }
    return (
      <div className="contest-programming-problem-page__widget">
        <StatementLanguageWidget defaultLanguage={response.defaultLanguage} statementLanguages={response.languages} />
      </div>
    );
  };

  const renderStatement = () => {
    if (!response) {
      return <LoadingState />;
    }

    const { problem, totalSubmissions, worksheet } = response;

    let submissionWarning;
    if (!!problem.submissionsLimit) {
      const submissionsLeft = problem.submissionsLimit - totalSubmissions;
      submissionWarning = '' + submissionsLeft + ' submissions left.';
    }

    return (
      <ProblemWorksheetCard
        alias={problem.alias}
        worksheet={worksheet}
        onSubmit={createSubmission}
        submissionWarning={submissionWarning}
        gradingLanguage={gradingLanguage}
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
