import { Alignment, Button, FormGroup, MenuItem } from '@blueprintjs/core';
import { CaretDown } from '@blueprintjs/icons';
import { Select } from '@blueprintjs/select';
import classNames from 'classnames';

import { getIntent, getIntentClassName } from '../meta';
import { FormInputValidation } from '../FormInputValidation/FormInputValidation';

export function FormSelect2({ input, className, label, meta, optionValues, optionNamesMap, small }) {
  const isUsingFilter = optionValues.length >= 10;

  const renderOption = (value, { handleClick, modifiers }) => {
    return <MenuItem active={modifiers.active} key={value} onClick={handleClick} text={optionNamesMap[value]} />;
  };

  const filterOption = (query, option) => {
    return option.toLowerCase().indexOf(query.toLowerCase()) >= 0;
  };

  const { onChange, ...inputProps } = input;

  return (
    <FormGroup className={className} labelFor={input.name} label={label} intent={getIntent(meta)}>
      <Select
        className={classNames('form-group__select', getIntentClassName(meta))}
        items={optionValues}
        itemPredicate={isUsingFilter ? filterOption : undefined}
        itemRenderer={renderOption}
        activeItem={inputProps.value}
        onItemSelect={onChange}
        inputProps={{ ...inputProps, autoComplete: 'off' }}
        filterable={isUsingFilter}
        popoverProps={{ usePortal: false }}
      >
        <Button
          data-key={inputProps.name}
          alignText={Alignment.LEFT}
          text={optionNamesMap[input.value]}
          rightIcon={<CaretDown />}
          small={small}
        />
      </Select>
      <FormInputValidation meta={meta} />
    </FormGroup>
  );
}
