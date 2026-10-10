import { Button, HTMLSelect, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useState } from 'react';

import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { ProblemStatementCard as BundleProblemStatementCard } from '../../../../components/ProblemWorksheetCard/Bundle/ProblemStatementCard/ProblemStatementCard';
import { ProblemWorksheetCard } from '../../../../components/ProblemWorksheetCard/Programming/ProblemWorksheetCard';
import { ProblemType } from '../../../../modules/api/problem';
import { formatItemMediaUrls } from '../../../../modules/api/problemItem';
import { formatStatementMediaUrls } from '../../../../modules/api/problemStatement';
import { worldLanguageNamesMap } from '../../../../modules/api/worldLanguage';
import { problemBySlugQueryOptions, problemQueryOptions } from '../../../../modules/queries/problem';
import { problemItemsQueryOptions } from '../../../../modules/queries/problemItem';
import {
  problemStatementLanguagesQueryOptions,
  problemStatementQueryOptions,
  updateProblemStatementMutationOptions,
} from '../../../../modules/queries/problemStatement';
import StatementEditForm from '../../catalog/StatementEditForm/StatementEditForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export default function ProblemStatementPage() {
  const { problemSlug } = useParams({ strict: false });

  const {
    data: { jid: problemJid },
  } = useSuspenseQuery(problemBySlugQueryOptions(problemSlug));

  const {
    data: { data: problem, config },
  } = useSuspenseQuery(problemQueryOptions(problemJid));

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

    if (isBundle) {
      return (
        <BundleProblemStatementCard
          statement={{ title: statement.title, text }}
          items={itemsResponse.data.map(item => formatItemMediaUrls(item, problemJid))}
          latestSubmissions={{}}
          disabled
        />
      );
    }
    return (
      <ProblemWorksheetCard
        worksheet={{ statement: { title: statement.title, text }, limits: {} }}
        showLimits={false}
      />
    );
  };

  const renderContent = () => {
    if (!statement || (isBundle && !itemsResponse)) {
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
