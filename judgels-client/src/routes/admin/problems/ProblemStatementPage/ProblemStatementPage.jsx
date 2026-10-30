import { Button, Intent } from '@blueprintjs/core';
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
import { baseStatementMediaURL, formatStatementMediaUrls } from '../../../../modules/api/problemStatement';
import {
  problemBySlugQueryOptions,
  problemPermissionsQueryOptions,
  problemQueryOptions,
} from '../../../../modules/queries/problem';
import { createProblemBundleSubmissionMutationOptions } from '../../../../modules/queries/problemBundleSubmission';
import { problemGradingConfigQueryOptions } from '../../../../modules/queries/problemGrading';
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
import { useSession } from '../../../../modules/session';
import LanguageSelect from '../../catalog/LanguageSelect/LanguageSelect';
import StatementEditForm from '../../catalog/StatementEditForm/StatementEditForm';

import * as toastActions from '../../../../modules/toast/toastActions';

import './ProblemStatementPage.scss';

export default function ProblemStatementPage() {
  const { problemSlug } = useParams({ strict: false });
  const { user } = useSession();
  const navigate = useNavigate();

  const {
    data: { jid: problemJid },
  } = useSuspenseQuery(problemBySlugQueryOptions(problemSlug));

  const { data: problem } = useSuspenseQuery(problemQueryOptions(problemJid));

  const {
    data: { reasonNotAllowedToSubmit },
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

  const { data: submissionConfig } = useQuery({
    ...problemSubmissionConfigQueryOptions(problemJid),
    enabled: !isBundle,
  });

  const { data: gradingConfig } = useQuery({
    ...problemGradingConfigQueryOptions(problemJid),
    enabled: !isBundle,
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
      <LanguageSelect
        languages={enabledLanguages}
        value={language}
        disabled={isEditing}
        onChange={setSelectedLanguage}
      />
    );
  };

  const renderEditButton = () => {
    return (
      statement &&
      !isEditing && (
        <Button intent={Intent.PRIMARY} icon={<Edit />} onClick={() => setIsEditing(true)}>
          Edit
        </Button>
      )
    );
  };

  const renderPreview = () => {
    const text = formatStatementMediaUrls(statement.text, problemJid, user.jid);

    if (isBundle) {
      return (
        <BundleProblemWorksheetCard
          worksheet={{
            statement: { title: statement.title, text },
            items: itemsResponse.data.map(item => formatItemMediaUrls(item, problemJid, user.jid)),
            reasonNotAllowedToSubmit,
          }}
          latestSubmissions={Object.fromEntries(
            Object.entries(answers).map(([itemJid, answer]) => [itemJid, { answer }])
          )}
          onAnswerItem={answerItem}
          onSubmit={createBundleSubmission}
          isSubmitting={createBundleSubmissionMutation.isPending}
        />
      );
    }
    return (
      <ProblemWorksheetCard
        worksheet={{
          statement: { title: statement.title, text },
          limits: gradingConfig.config,
          submissionConfig,
          reasonNotAllowedToSubmit,
        }}
        onSubmit={createSubmission}
      />
    );
  };

  const renderContent = () => {
    if (!statement || (isBundle && !itemsResponse) || (!isBundle && (!submissionConfig || !gradingConfig))) {
      return <LoadingState />;
    }
    if (isEditing) {
      return (
        <StatementEditForm
          initialValues={statement}
          onSubmit={updateStatement}
          onCancel={() => setIsEditing(false)}
          baseMediaURL={baseStatementMediaURL(problemJid, user.jid)}
        />
      );
    }
    return renderPreview();
  };

  return (
    <Flex className="problem-statement-page" flexDirection="column" gap={2} width={100} marginX="auto">
      <Flex justifyContent="end" alignItems="center" gap={2}>
        {renderLanguageSelect()}
        {renderEditButton()}
      </Flex>
      {renderContent()}
    </Flex>
  );
}
