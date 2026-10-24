import { Button, HTMLTable, Intent, Tag } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import { useState } from 'react';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { worldLanguageNamesMap } from '../../../../modules/api/worldLanguage';
import LanguageSelect from '../LanguageSelect/LanguageSelect';

import * as toastActions from '../../../../modules/toast/toastActions';

// The languages a statement or an editorial is written in: their listing, with add, enable, disable and make default.
export default function LanguagesPanel({
  enabledLanguages,
  disabledLanguages,
  defaultLanguage,
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
    return (
      <Flex gap={2} alignItems="center">
        <LanguageSelect
          languages={addableLanguages}
          value={languageToAdd}
          label="Add language"
          placeholder="Add language..."
          filterable
          onChange={setLanguageToAdd}
        />
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
    if (language !== defaultLanguage) {
      return status;
    }
    return (
      <Flex gap={2} alignItems="center">
        {status}
        <Tag minimal intent={Intent.PRIMARY}>
          Default
        </Tag>
      </Flex>
    );
  };

  return (
    <Flex flexDirection="column" gap={2}>
      <h4>Languages</h4>
      <Flex flexDirection="column" gap={5}>
        {renderAddForm()}
        <HTMLTable striped className="table-list-condensed">
          <thead>
            <tr>
              <th>Language</th>
              <th>Status</th>
              <th className="col-fit" />
            </tr>
          </thead>
          <tbody>
            {languages.map(language => (
              <tr key={language}>
                <td>{worldLanguageNamesMap[language] || language}</td>
                <td>{renderStatus(language)}</td>
                <td className="col-fit" style={{ textAlign: 'right' }}>
                  {renderActions(language)}
                </td>
              </tr>
            ))}
          </tbody>
        </HTMLTable>
      </Flex>
    </Flex>
  );
}
