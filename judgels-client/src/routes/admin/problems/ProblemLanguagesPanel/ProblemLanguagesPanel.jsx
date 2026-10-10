import { Button, HTMLSelect, HTMLTable, Intent } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import { useState } from 'react';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { worldLanguageNamesMap } from '../../../../modules/api/worldLanguage';

import * as toastActions from '../../../../modules/toast/toastActions';

// The languages a problem's statement or editorial is written in: their listing, with add, enable, disable and make default.
export default function ProblemLanguagesPanel({
  enabledLanguages,
  disabledLanguages,
  defaultLanguage,
  canEdit,
  onAddLanguage,
  onEnableLanguage,
  onDisableLanguage,
  onMakeLanguageDefault,
}) {
  const [languageToAdd, setLanguageToAdd] = useState('');
  const [isAdding, setIsAdding] = useState(false);
  const [isUpdating, setIsUpdating] = useState(false);

  const isMutating = isAdding || isUpdating;

  const languages = [...enabledLanguages, ...disabledLanguages].sort();
  const addableLanguages = Object.keys(worldLanguageNamesMap).filter(language => !languages.includes(language));

  const addLanguage = async () => {
    setIsAdding(true);
    try {
      await onAddLanguage(languageToAdd);
      setLanguageToAdd('');
      toastActions.showSuccessToast('Language added.');
    } finally {
      setIsAdding(false);
    }
  };

  const updateLanguage = async (onUpdateLanguage, language) => {
    setIsUpdating(true);
    try {
      await onUpdateLanguage(language);
    } finally {
      setIsUpdating(false);
    }
  };

  const renderAddForm = () => {
    if (!canEdit) {
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
          loading={isAdding}
          onClick={addLanguage}
        />
      </Flex>
    );
  };

  const renderActions = language => {
    if (!canEdit) {
      return null;
    }
    if (disabledLanguages.includes(language)) {
      return (
        <Button small text="Enable" disabled={isMutating} onClick={() => updateLanguage(onEnableLanguage, language)} />
      );
    }
    if (language === defaultLanguage) {
      return null;
    }
    return (
      <ActionButtons justifyContent="end">
        <Button
          small
          text="Disable"
          disabled={isMutating}
          onClick={() => updateLanguage(onDisableLanguage, language)}
        />
        <Button
          small
          text="Make default"
          disabled={isMutating}
          onClick={() => updateLanguage(onMakeLanguageDefault, language)}
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
