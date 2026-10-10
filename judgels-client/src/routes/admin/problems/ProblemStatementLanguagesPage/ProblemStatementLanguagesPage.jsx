import { Button, HTMLSelect, HTMLTable, Intent } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useState } from 'react';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { worldLanguageNamesMap } from '../../../../modules/api/worldLanguage';
import { problemQueryOptions } from '../../../../modules/queries/problem';
import {
  addProblemStatementLanguageMutationOptions,
  disableProblemStatementLanguageMutationOptions,
  enableProblemStatementLanguageMutationOptions,
  makeProblemStatementLanguageDefaultMutationOptions,
  problemStatementLanguagesQueryOptions,
} from '../../../../modules/queries/problemStatement';

import * as toastActions from '../../../../modules/toast/toastActions';

export default function ProblemStatementLanguagesPage() {
  const { problemJid } = useParams({ strict: false });

  const {
    data: { config },
  } = useSuspenseQuery(problemQueryOptions(problemJid));

  const {
    data: { enabledLanguages, disabledLanguages, defaultLanguage },
  } = useSuspenseQuery(problemStatementLanguagesQueryOptions(problemJid));

  const addLanguageMutation = useMutation(addProblemStatementLanguageMutationOptions(problemJid));
  const enableLanguageMutation = useMutation(enableProblemStatementLanguageMutationOptions(problemJid));
  const disableLanguageMutation = useMutation(disableProblemStatementLanguageMutationOptions(problemJid));
  const makeLanguageDefaultMutation = useMutation(makeProblemStatementLanguageDefaultMutationOptions(problemJid));

  const [languageToAdd, setLanguageToAdd] = useState('');

  const isMutating =
    addLanguageMutation.isPending ||
    enableLanguageMutation.isPending ||
    disableLanguageMutation.isPending ||
    makeLanguageDefaultMutation.isPending;

  const languages = [...enabledLanguages, ...disabledLanguages].sort();
  const addableLanguages = Object.keys(worldLanguageNamesMap).filter(language => !languages.includes(language));

  const addLanguage = () => {
    addLanguageMutation.mutate(languageToAdd, {
      onSuccess: () => {
        setLanguageToAdd('');
        toastActions.showSuccessToast('Language added.');
      },
    });
  };

  const renderAddForm = () => {
    if (!config.canEdit) {
      return null;
    }
    return (
      <Flex gap={2} alignItems="center">
        <HTMLSelect aria-label="Add language" value={languageToAdd} onChange={e => setLanguageToAdd(e.target.value)}>
          <option value="">Add language...</option>
          {addableLanguages.map(language => (
            <option key={language} value={language}>
              {worldLanguageNamesMap[language]}
            </option>
          ))}
        </HTMLSelect>
        <Button
          text="Add"
          intent={Intent.PRIMARY}
          disabled={!languageToAdd || isMutating}
          loading={addLanguageMutation.isPending}
          onClick={addLanguage}
        />
      </Flex>
    );
  };

  const renderActions = language => {
    if (!config.canEdit) {
      return null;
    }
    if (disabledLanguages.includes(language)) {
      return (
        <Button small text="Enable" disabled={isMutating} onClick={() => enableLanguageMutation.mutate(language)} />
      );
    }
    if (language === defaultLanguage) {
      return null;
    }
    return (
      <ActionButtons justifyContent="end">
        <Button small text="Disable" disabled={isMutating} onClick={() => disableLanguageMutation.mutate(language)} />
        <Button
          small
          text="Make default"
          disabled={isMutating}
          onClick={() => makeLanguageDefaultMutation.mutate(language)}
        />
      </ActionButtons>
    );
  };

  const renderStatus = language => {
    const status = disabledLanguages.includes(language) ? 'Disabled' : 'Enabled';
    return language === defaultLanguage ? `${status} (default)` : status;
  };

  return (
    <Flex flexDirection="column" gap={2}>
      <Flex asChild justifyContent="space-between" alignItems="baseline">
        <h4>
          <span>Languages</span>
          {renderAddForm()}
        </h4>
      </Flex>
      <HTMLTable striped className="table-list-condensed">
        <thead>
          <tr>
            <th>Language</th>
            <th>Status</th>
            <th />
          </tr>
        </thead>
        <tbody>
          {languages.map(language => (
            <tr key={language}>
              <td>{worldLanguageNamesMap[language] || language}</td>
              <td>{renderStatus(language)}</td>
              <td style={{ textAlign: 'right' }}>{renderActions(language)}</td>
            </tr>
          ))}
        </tbody>
      </HTMLTable>
    </Flex>
  );
}
