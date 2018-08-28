import { Alignment, Button, FormGroup, MenuItem } from '@blueprintjs/core';
import { Select } from '@blueprintjs/select';
import * as classNames from 'classnames';
import * as React from 'react';

import { getIntent, getIntentClassName } from '../meta';
import { FormInputProps } from '../props';
import { FormInputValidation } from '../FormInputValidation/FormInputValidation';

export interface FormSelect2Props extends FormInputProps {
  optionValues: string[];
  optionNamesMap: { [key: string]: string };
}

export class FormSelect2 extends React.PureComponent<FormSelect2Props> {
  render() {
    const SelectC = Select.ofType<string>();
    const { onChange, ...inputProps } = this.props.input;

    return (
      <FormGroup
        className={this.props.className}
        labelFor={this.props.input.name}
        label={this.props.label}
        intent={getIntent(this.props.meta)}
      >
        <SelectC
          className={classNames('form-group__select', getIntentClassName(this.props.meta))}
          items={this.props.optionValues}
          itemRenderer={this.renderOption as any}
          onItemSelect={onChange}
          inputProps={inputProps}
          filterable={false}
          popoverProps={{ usePortal: false }}
        >
          <Button
            alignText={Alignment.LEFT}
            text={this.props.optionNamesMap[this.props.input.value]}
            rightIcon="caret-down"
          />
        </SelectC>
        <FormInputValidation meta={this.props.meta} />
      </FormGroup>
    );
  }

  private renderOption = (value: string, { handleClick, modifiers }) => {
    return (
      <MenuItem active={modifiers.active} key={value} onClick={handleClick} text={this.props.optionNamesMap[value]} />
    );
  };
}
