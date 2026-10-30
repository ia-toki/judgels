import { Alignment, Button, MenuItem } from '@blueprintjs/core';
import { CaretDown } from '@blueprintjs/icons';
import { Select } from '@blueprintjs/select';

import './OptionSelect.scss';

export function OptionSelect({
  options,
  value,
  getOptionName,
  label,
  placeholder,
  filterable = false,
  filterLabel = 'Filter',
  noResultsText = 'Nothing found.',
  disabled,
  onChange,
}) {
  const filterOption = (query, option) => getOptionName(option).toLowerCase().includes(query.toLowerCase());

  const renderOption = (option, { handleClick, modifiers }) => {
    if (!modifiers.matchesPredicate) {
      return null;
    }
    return (
      <MenuItem
        key={option}
        roleStructure="listoption"
        active={modifiers.active}
        selected={option === value}
        onClick={handleClick}
        text={getOptionName(option)}
      />
    );
  };

  return (
    <Select
      items={options}
      itemRenderer={renderOption}
      itemPredicate={filterOption}
      activeItem={value || null}
      onItemSelect={onChange}
      filterable={filterable}
      inputProps={{ 'aria-label': filterLabel, placeholder: 'Filter...' }}
      noResults={<MenuItem roleStructure="listoption" disabled text={noResultsText} />}
      resetOnClose
      disabled={disabled}
      popoverTargetProps={{ 'aria-label': label }}
    >
      <Button
        className="option-select__button"
        disabled={disabled}
        alignText={Alignment.LEFT}
        ellipsizeText
        text={value ? getOptionName(value) : placeholder}
        endIcon={<CaretDown />}
      />
    </Select>
  );
}
