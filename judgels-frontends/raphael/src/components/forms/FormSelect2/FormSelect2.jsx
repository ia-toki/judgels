import { Alignment, Button, FormGroup, MenuItem } from '@blueprintjs/core';
import { Select } from '@blueprintjs/select';
import classNames from 'classnames';

import { getIntent, getIntentClassName } from '../meta';
import { FormInputValidation } from '../FormInputValidation/FormInputValidation';

export function FormSelect2({ input, className, label, meta, optionValues, optionNamesMap }) {
  const renderOption = (value, { handleClick, modifiers }) => {
    return <MenuItem active={modifiers.active} key={value} onClick={handleClick} text={optionNamesMap[value]} />;
  };

  const { onChange, ...inputProps } = input;

  return (
    <FormGroup className={className} labelFor={input.name} label={label} intent={getIntent(meta)}>
      <Select
        className={classNames('form-group__select', getIntentClassName(meta))}
        items={optionValues}
        itemRenderer={renderOption}
        onItemSelect={onChange}
        inputProps={inputProps}
        filterable={false}
        popoverProps={{ usePortal: false }}
      >
        <Button
          data-key={inputProps.name}
          alignText={Alignment.LEFT}
          text={optionNamesMap[input.value]}
          rightIcon="caret-down"
        />
      </Select>
      <FormInputValidation meta={meta} />
    </FormGroup>
  );
}
