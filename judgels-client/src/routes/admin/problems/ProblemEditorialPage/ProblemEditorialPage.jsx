import { Button, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useState } from 'react';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { ProblemEditorial } from '../../../../components/ProblemEditorial/ProblemEditorial';
import { formatEditorialMediaUrls } from '../../../../modules/api/problemEditorial';
import { problemBySlugQueryOptions } from '../../../../modules/queries/problem';
import {
  problemEditorialLanguagesQueryOptions,
  problemEditorialQueryOptions,
  updateProblemEditorialMutationOptions,
} from '../../../../modules/queries/problemEditorial';
import LanguageSelect from '../../catalog/LanguageSelect/LanguageSelect';
import ProblemEditorialEditForm from '../ProblemEditorialEditForm/ProblemEditorialEditForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export default function ProblemEditorialPage() {
  const { problemSlug } = useParams({ strict: false });

  const {
    data: { jid: problemJid },
  } = useSuspenseQuery(problemBySlugQueryOptions(problemSlug));

  const {
    data: { enabledLanguages, defaultLanguage },
  } = useSuspenseQuery(problemEditorialLanguagesQueryOptions(problemJid));

  const [selectedLanguage, setSelectedLanguage] = useState(defaultLanguage);
  const language = enabledLanguages.includes(selectedLanguage) ? selectedLanguage : defaultLanguage;

  const { data: editorial } = useQuery(problemEditorialQueryOptions(problemJid, { language }));

  const updateEditorialMutation = useMutation(updateProblemEditorialMutationOptions(problemJid, language));

  const [isEditing, setIsEditing] = useState(false);

  const updateEditorial = async data => {
    await updateEditorialMutation.mutateAsync(
      { text: data.text || '' },
      {
        onSuccess: () => toastActions.showSuccessToast('Editorial updated.'),
      }
    );
    setIsEditing(false);
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
      editorial &&
      !isEditing && (
        <Button intent={Intent.PRIMARY} icon={<Edit />} onClick={() => setIsEditing(true)}>
          Edit
        </Button>
      )
    );
  };

  const renderContent = () => {
    if (!editorial) {
      return <LoadingState />;
    }
    if (isEditing) {
      return (
        <ProblemEditorialEditForm
          initialValues={editorial}
          onSubmit={updateEditorial}
          onCancel={() => setIsEditing(false)}
        />
      );
    }
    return (
      <ContentCard>
        <ProblemEditorial>{formatEditorialMediaUrls(editorial.text, problemJid)}</ProblemEditorial>
      </ContentCard>
    );
  };

  return (
    <Flex flexDirection="column" gap={2}>
      <Flex justifyContent="end" alignItems="center" gap={2}>
        {renderLanguageSelect()}
        {renderEditButton()}
      </Flex>
      {renderContent()}
    </Flex>
  );
}
