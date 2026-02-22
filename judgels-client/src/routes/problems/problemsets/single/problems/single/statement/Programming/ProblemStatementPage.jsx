import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget from '../../../../../../../../components/LanguageWidget/StatementLanguageWidget';
import { ProblemWorksheetCard } from '../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemWorksheetCard';
import { sendGAEvent } from '../../../../../../../../ga';
import { getGradingLanguageFamily } from '../../../../../../../../modules/api/gabriel/language.js';
import { getNavigationRef } from '../../../../../../../../modules/navigation/navigationRef';
import {
  problemSetBySlugQueryOptions,
  problemSetProblemQueryOptions,
} from '../../../../../../../../modules/queries/problemSet';
import { createProblemSetProgrammingSubmissionMutationOptions } from '../../../../../../../../modules/queries/problemSetSubmissionProgramming';
import { useWebPrefs } from '../../../../../../../../modules/webPrefs';

import { toastActions } from '../../../../../../../../modules/toast/toastActions';

export default function ProblemStatementPage({ worksheet }) {
  const { problemSetSlug, problemAlias } = useParams({ strict: false });
  const { data: problemSet } = useSuspenseQuery(problemSetBySlugQueryOptions(problemSetSlug));
  const { data: problem } = useSuspenseQuery(problemSetProblemQueryOptions(problemSet.jid, problemAlias));
  const { gradingLanguage, setGradingLanguage } = useWebPrefs();

  const createSubmissionMutation = useMutation(
    createProblemSetProgrammingSubmissionMutationOptions(problemSet.jid, worksheet.problem.problemJid)
  );

  const renderStatementLanguageWidget = () => {
    const { defaultLanguage, languages } = worksheet;
    if (!defaultLanguage || !languages) {
      return null;
    }
    const props = {
      defaultLanguage: defaultLanguage,
      statementLanguages: languages,
    };
    return (
      <div className="language-widget-wrapper">
        <StatementLanguageWidget {...props} />
      </div>
    );
  };

  const renderStatement = () => {
    return (
      <ProblemWorksheetCard
        worksheet={worksheet.worksheet}
        onSubmit={createSubmission}
        gradingLanguage={gradingLanguage}
      />
    );
  };

  const createSubmission = async data => {
    setGradingLanguage(data.gradingLanguage);

    sendGAEvent({ category: 'Problems', action: 'Submit problemset problem', label: problemSet.name });
    sendGAEvent({
      category: 'Problems',
      action: 'Submit problem',
      label: problemSet.name + ': ' + problem.alias,
    });
    if (getGradingLanguageFamily(data.gradingLanguage)) {
      sendGAEvent({
        category: 'Problems',
        action: 'Submit language',
        label: getGradingLanguageFamily(data.gradingLanguage),
      });
    }

    await createSubmissionMutation.mutateAsync(data, {
      onSuccess: () => {
        toastActions.showSuccessToast('Solution submitted.');
        window.scrollTo(0, 0);
        getNavigationRef().push(`/problems/${problemSet.slug}/${problem.alias}/submissions/mine`);
      },
    });
  };

  return (
    <ContentCard>
      {renderStatementLanguageWidget()}
      {renderStatement()}
    </ContentCard>
  );
}
