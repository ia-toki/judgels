import { Button, MenuItem } from '@blueprintjs/core';
import { Select } from '@blueprintjs/select';
import * as classNames from 'classnames';
import * as React from 'react';

import { getIntentClassName } from '../meta';
import { FormInputProps } from '../props';
import { FormTableInput } from '../FormTableInput/FormTableInput';

export interface FormTableSelect2Props extends FormInputProps {
  optionValues: string[];
  optionNamesMap: { [key: string]: string };
}

export class FormTableSelect2 extends React.PureComponent<FormTableSelect2Props> {
  render() {
    const SelectC = Select.ofType<string>();
    const { onChange, ...inputProps } = this.props.input;

    return (
      <FormTableInput {...this.props}>
        <SelectC
          className={classNames(getIntentClassName(this.props.meta))}
          items={this.props.optionValues}
          itemRenderer={this.renderOption as any}
          onItemSelect={onChange}
          inputProps={inputProps}
          filterable={false}
          popoverProps={{ usePortal: false }}
        >
          <Button text={this.props.optionNamesMap[this.props.input.value]} rightIcon="caret-down" />
        </SelectC>
      </FormTableInput>
    );
  }

  private renderOption = (value: string, { handleClick, modifiers }) => {
    return (
      <MenuItem active={modifiers.active} key={value} onClick={handleClick} text={this.props.optionNamesMap[value]} />
    );
  };
}
