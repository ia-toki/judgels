import { Alignment, Button, MenuItem } from '@blueprintjs/core';
import { CaretDown } from '@blueprintjs/icons';
import { Select } from '@blueprintjs/select';

import { languageDisplayNamesMap } from '../../../modules/api/language';

import './LanguageSelect.scss';

export default function LanguageSelect({ value, languages, onChange }) {
  const renderOption = (language, { handleClick, modifiers }) => {
    return (
      <MenuItem
        active={modifiers.active}
        key={language}
        onClick={handleClick}
        text={languageDisplayNamesMap[language]}
      />
    );
  };

  return (
    <Select
      className="language-select"
      items={languages}
      itemRenderer={renderOption}
      activeItem={value}
      onItemSelect={onChange}
      filterable={false}
      popoverProps={{ usePortal: false, targetTagName: 'span' }}
    >
      <Button
        disabled={languages.length <= 1}
        alignText={Alignment.LEFT}
        text={languageDisplayNamesMap[value]}
        rightIcon={<CaretDown />}
      />
    </Select>
  );
}
