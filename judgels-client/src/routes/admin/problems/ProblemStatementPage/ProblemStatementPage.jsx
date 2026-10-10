import { Button, HTMLSelect, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useState } from 'react';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { ProblemWorksheetCard } from '../../../../components/ProblemWorksheetCard/Programming/ProblemWorksheetCard';
import RichStatementText from '../../../../components/RichStatementText/RichStatementText';
import { ProblemType } from '../../../../modules/api/problem';
import { formatStatementMediaUrls } from '../../../../modules/api/problemStatement';
import { worldLanguageNamesMap } from '../../../../modules/api/worldLanguage';
import { problemQueryOptions } from '../../../../modules/queries/problem';
import {
  problemStatementLanguagesQueryOptions,
  problemStatementQueryOptions,
  updateProblemStatementMutationOptions,
} from '../../../../modules/queries/problemStatement';
import ProblemStatementEditForm from '../ProblemStatementEditForm/ProblemStatementEditForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export default function ProblemStatementPage() {
  const { problemJid } = useParams({ strict: false });

  const {
    data: { data: problem, config },
  } = useSuspenseQuery(problemQueryOptions(problemJid));

  const {
    data: { enabledLanguages, defaultLanguage },
  } = useSuspenseQuery(problemStatementLanguagesQueryOptions(problemJid));

  const [selectedLanguage, setSelectedLanguage] = useState(defaultLanguage);
  const language = enabledLanguages.includes(selectedLanguage) ? selectedLanguage : defaultLanguage;

  const { data: statement } = useQuery(problemStatementQueryOptions(problemJid, { language }));

  const updateStatementMutation = useMutation(updateProblemStatementMutationOptions(problemJid, language));

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

  const renderPreview = () => {
    const text = formatStatementMediaUrls(statement.text, problemJid);

    if (problem.type === ProblemType.Programming) {
      return (
        <ProblemWorksheetCard
          worksheet={{ statement: { title: statement.title, text }, limits: {} }}
          showLimits={false}
        />
      );
    }
    return (
      <ContentCard>
        <h2>{statement.title}</h2>
        <RichStatementText>{text}</RichStatementText>
      </ContentCard>
    );
  };

  const renderContent = () => {
    if (!statement) {
      return <LoadingState />;
    }
    if (isEditing) {
      return (
        <ProblemStatementEditForm
          initialValues={statement}
          onSubmit={updateStatement}
          onCancel={() => setIsEditing(false)}
        />
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
