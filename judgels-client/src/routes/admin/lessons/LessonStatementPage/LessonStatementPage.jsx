import { Button, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useState } from 'react';

import { LessonStatementCard } from '../../../../components/LessonStatementCard/LessonStatementCard';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { formatStatementMediaUrls } from '../../../../modules/api/lessonStatement';
import { lessonBySlugQueryOptions } from '../../../../modules/queries/lesson';
import {
  lessonStatementLanguagesQueryOptions,
  lessonStatementQueryOptions,
  updateLessonStatementMutationOptions,
} from '../../../../modules/queries/lessonStatement';
import LanguageSelect from '../../catalog/LanguageSelect/LanguageSelect';
import StatementEditForm from '../../catalog/StatementEditForm/StatementEditForm';

import * as toastActions from '../../../../modules/toast/toastActions';

import './LessonStatementPage.scss';

export default function LessonStatementPage() {
  const { lessonSlug } = useParams({ strict: false });

  const {
    data: { jid: lessonJid },
  } = useSuspenseQuery(lessonBySlugQueryOptions(lessonSlug));

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
    <Flex className="lesson-statement-page" flexDirection="column" gap={2} width={100} marginX="auto">
      <Flex justifyContent="end" alignItems="center" gap={2}>
        {renderLanguageSelect()}
        {renderEditButton()}
      </Flex>
      {renderContent()}
    </Flex>
  );
}
