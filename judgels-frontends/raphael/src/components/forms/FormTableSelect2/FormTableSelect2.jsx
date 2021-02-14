import { Alignment, Button, MenuItem } from '@blueprintjs/core';
import { Select } from '@blueprintjs/select';
import classNames from 'classnames';

import { getIntentClassName } from '../meta';
import { FormTableInput } from '../FormTableInput/FormTableInput';

import './FormTableSelect2.css';

export function FormTableSelect2(props) {
  const { input, meta, optionValues, optionNamesMap } = props;
  const { onChange, ...inputProps } = input;

  const renderOption = (value, { handleClick, modifiers }) => {
    return (
      <MenuItem
        active={modifiers.active}
        key={value}
        data-key={value}
        onClick={handleClick}
        text={optionNamesMap[value]}
      />
    );
  };

  return (
    <FormTableInput {...props}>
      <Select
        className={classNames(getIntentClassName(meta))}
        items={optionValues}
        itemRenderer={renderOption}
        activeItem={inputProps.value}
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
    </FormTableInput>
  );
}
