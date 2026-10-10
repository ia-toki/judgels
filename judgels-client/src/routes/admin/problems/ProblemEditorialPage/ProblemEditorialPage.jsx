import { Button, HTMLSelect, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useState } from 'react';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { ProblemEditorial } from '../../../../components/ProblemEditorial/ProblemEditorial';
import { formatEditorialMediaUrls } from '../../../../modules/api/problemEditorial';
import { worldLanguageNamesMap } from '../../../../modules/api/worldLanguage';
import { problemQueryOptions } from '../../../../modules/queries/problem';
import {
  problemEditorialLanguagesQueryOptions,
  problemEditorialQueryOptions,
  updateProblemEditorialMutationOptions,
} from '../../../../modules/queries/problemEditorial';
import ProblemEditorialEditForm from '../ProblemEditorialEditForm/ProblemEditorialEditForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export default function ProblemEditorialPage() {
  const { problemJid } = useParams({ strict: false });

  const {
    data: { data: problem, setterJidsMap, profilesMap, config },
  } = useSuspenseQuery(problemQueryOptions(problemJid));

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
      config.canEdit &&
      editorial &&
      !isEditing && (
        <Button small intent={Intent.PRIMARY} icon={<Edit />} onClick={() => setIsEditing(true)}>
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
        <ProblemEditorial title={problem.slug} settersMap={setterJidsMap} profilesMap={profilesMap}>
          {formatEditorialMediaUrls(editorial.text, problemJid)}
        </ProblemEditorial>
      </ContentCard>
    );
  };

  return (
    <div>
      <Flex asChild justifyContent="space-between" alignItems="baseline">
        <h4>
          <span>Content</span>
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
