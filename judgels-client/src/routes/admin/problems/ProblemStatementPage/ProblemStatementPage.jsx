import { Button, HTMLSelect, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useNavigate, useParams } from '@tanstack/react-router';
import { useState } from 'react';

import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { ProblemWorksheetCard as BundleProblemWorksheetCard } from '../../../../components/ProblemWorksheetCard/Bundle/ProblemWorksheetCard';
import { ProblemWorksheetCard } from '../../../../components/ProblemWorksheetCard/Programming/ProblemWorksheetCard';
import { ProblemType } from '../../../../modules/api/problem';
import { formatItemMediaUrls } from '../../../../modules/api/problemItem';
import { formatStatementMediaUrls } from '../../../../modules/api/problemStatement';
import { worldLanguageNamesMap } from '../../../../modules/api/worldLanguage';
import {
  problemBySlugQueryOptions,
  problemPermissionsQueryOptions,
  problemQueryOptions,
} from '../../../../modules/queries/problem';
import { createProblemBundleSubmissionMutationOptions } from '../../../../modules/queries/problemBundleSubmission';
import { problemItemsQueryOptions } from '../../../../modules/queries/problemItem';
import {
  problemStatementLanguagesQueryOptions,
  problemStatementQueryOptions,
  updateProblemStatementMutationOptions,
} from '../../../../modules/queries/problemStatement';
import {
  createProblemSubmissionMutationOptions,
  problemSubmissionConfigQueryOptions,
} from '../../../../modules/queries/problemSubmission';
import StatementEditForm from '../../catalog/StatementEditForm/StatementEditForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export default function ProblemStatementPage() {
  const { problemSlug } = useParams({ strict: false });
  const navigate = useNavigate();

  const {
    data: { jid: problemJid },
  } = useSuspenseQuery(problemBySlugQueryOptions(problemSlug));

  const { data: problem } = useSuspenseQuery(problemQueryOptions(problemJid));

  const {
    data: { canEdit, reasonNotAllowedToSubmit },
  } = useSuspenseQuery(problemPermissionsQueryOptions(problemJid));

  const {
    data: { enabledLanguages, defaultLanguage },
  } = useSuspenseQuery(problemStatementLanguagesQueryOptions(problemJid));

  const [selectedLanguage, setSelectedLanguage] = useState(defaultLanguage);
  const language = enabledLanguages.includes(selectedLanguage) ? selectedLanguage : defaultLanguage;

  const { data: statement } = useQuery(problemStatementQueryOptions(problemJid, { language }));

  // A bundle problem is previewed together with its items.
  const isBundle = problem.type === ProblemType.Bundle;
  const { data: itemsResponse } = useQuery({
    ...problemItemsQueryOptions(problemJid, { language }),
    enabled: isBundle,
  });

  // Only an editor can make test submissions, so nobody else is shown the form.
  const { data: submissionConfig } = useQuery({
    ...problemSubmissionConfigQueryOptions(problemJid),
    enabled: canEdit && !isBundle,
  });

  // A bundle problem is answered as a whole: the answers are kept here until they are submitted together.
  const [answers, setAnswers] = useState({});

  const updateStatementMutation = useMutation(updateProblemStatementMutationOptions(problemJid, language));
  const createSubmissionMutation = useMutation(createProblemSubmissionMutationOptions(problemJid));
  const createBundleSubmissionMutation = useMutation(createProblemBundleSubmissionMutationOptions(problemJid));

  const [isEditing, setIsEditing] = useState(false);

  const updateStatement = async data => {
    await updateStatementMutation.mutateAsync(
      { title: data.title, text: data.text || '' },
      {
        onSuccess: () => toastActions.showSuccessToast('Statement updated.'),
      }
    );
    setIsEditing(false);
  };

  const goToSubmissions = () => {
    toastActions.showSuccessToast('Solution submitted.');
    navigate({ to: `/admin/problems/${problemSlug}/submissions` });
  };

  const createSubmission = async data => {
    await createSubmissionMutation.mutateAsync(data, { onSuccess: goToSubmissions });
  };

  const createBundleSubmission = () => {
    createBundleSubmissionMutation.mutate({ answers, languageCode: language }, { onSuccess: goToSubmissions });
  };

  const answerItem = (itemJid, answer) => {
    setAnswers(prevAnswers => ({ ...prevAnswers, [itemJid]: answer }));
  };

  const renderLanguageSelect = () => {
    return (
      <HTMLSelect
        aria-label="Language"
        value={language}
        disabled={isEditing}
        onChange={e => setSelectedLanguage(e.target.value)}
      >
        {enabledLanguages
          .slice()
          .sort()
          .map(lang => (
            <option key={lang} value={lang}>
              {worldLanguageNamesMap[lang] || lang}
            </option>
          ))}
      </HTMLSelect>
    );
  };

  const renderEditButton = () => {
    return (
      canEdit &&
      statement &&
      !isEditing && (
        <Button small intent={Intent.PRIMARY} icon={<Edit />} onClick={() => setIsEditing(true)}>
          Edit
        </Button>
      )
    );
  };

  const renderPreview = () => {
    const text = formatStatementMediaUrls(statement.text, problemJid);

    if (isBundle) {
      return (
        <BundleProblemWorksheetCard
          worksheet={{
            statement: { title: statement.title, text },
            items: itemsResponse.data.map(item => formatItemMediaUrls(item, problemJid)),
            reasonNotAllowedToSubmit: canEdit ? reasonNotAllowedToSubmit : undefined,
          }}
          latestSubmissions={Object.fromEntries(
            Object.entries(answers).map(([itemJid, answer]) => [itemJid, { answer }])
          )}
          onAnswerItem={answerItem}
          onSubmit={canEdit ? createBundleSubmission : undefined}
          isSubmitting={createBundleSubmissionMutation.isPending}
          disabled={!canEdit}
        />
      );
    }
    return (
      <ProblemWorksheetCard
        worksheet={{
          statement: { title: statement.title, text },
          limits: {},
          submissionConfig,
          reasonNotAllowedToSubmit,
        }}
        showLimits={false}
        onSubmit={canEdit ? createSubmission : undefined}
      />
    );
  };

  const renderContent = () => {
    if (!statement || (isBundle && !itemsResponse) || (canEdit && !isBundle && !submissionConfig)) {
      return <LoadingState />;
    }
    if (isEditing) {
      return (
        <StatementEditForm initialValues={statement} onSubmit={updateStatement} onCancel={() => setIsEditing(false)} />
      );
    }
    return renderPreview();
  };

  return (
    <div>
      <Flex asChild justifyContent="space-between" alignItems="baseline">
        <h4>
          <span>Statements</span>
          <Flex gap={2} alignItems="center">
            {renderLanguageSelect()}
            {renderEditButton()}
          </Flex>
        </h4>
      </Flex>
      {renderContent()}
    </div>
  );
}
