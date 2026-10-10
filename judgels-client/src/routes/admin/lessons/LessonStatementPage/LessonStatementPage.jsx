import { Button, HTMLSelect, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useState } from 'react';

import { LessonStatementCard } from '../../../../components/LessonStatementCard/LessonStatementCard';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { formatStatementMediaUrls } from '../../../../modules/api/lessonStatement';
import { worldLanguageNamesMap } from '../../../../modules/api/worldLanguage';
import { lessonBySlugQueryOptions, lessonQueryOptions } from '../../../../modules/queries/lesson';
import {
  lessonStatementLanguagesQueryOptions,
  lessonStatementQueryOptions,
  updateLessonStatementMutationOptions,
} from '../../../../modules/queries/lessonStatement';
import StatementEditForm from '../../catalog/StatementEditForm/StatementEditForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export default function LessonStatementPage() {
  const { lessonSlug } = useParams({ strict: false });

  const {
    data: { jid: lessonJid },
  } = useSuspenseQuery(lessonBySlugQueryOptions(lessonSlug));

  const {
    data: { config },
  } = useSuspenseQuery(lessonQueryOptions(lessonJid));

  const {
    data: { enabledLanguages, defaultLanguage },
  } = useSuspenseQuery(lessonStatementLanguagesQueryOptions(lessonJid));

  const [selectedLanguage, setSelectedLanguage] = useState(defaultLanguage);
  const language = enabledLanguages.includes(selectedLanguage) ? selectedLanguage : defaultLanguage;

  const { data: statement } = useQuery(lessonStatementQueryOptions(lessonJid, { language }));

  const updateStatementMutation = useMutation(updateLessonStatementMutationOptions(lessonJid, language));

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
      statement &&
      !isEditing && (
        <Button small intent={Intent.PRIMARY} icon={<Edit />} onClick={() => setIsEditing(true)}>
          Edit
        </Button>
      )
    );
  };

  const renderContent = () => {
    if (!statement) {
      return <LoadingState />;
    }
    if (isEditing) {
      return (
        <StatementEditForm initialValues={statement} onSubmit={updateStatement} onCancel={() => setIsEditing(false)} />
      );
    }
    return (
      <LessonStatementCard
        statement={{ title: statement.title, text: formatStatementMediaUrls(statement.text, lessonJid) }}
      />
    );
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
