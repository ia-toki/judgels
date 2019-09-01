import { Checkbox } from '@blueprintjs/core';
import classNames from 'classnames';
import * as React from 'react';

import { getIntentClassName } from '../meta';
import { FormInputProps } from '../props';

import './FormCheckbox.css';

export interface FormCheckboxProps extends FormInputProps {
  small?: boolean;
}

export const FormCheckbox = (props: FormCheckboxProps) => {
  const { value, onChange, ...inputProps } = props.input;
  const newOnChange = ({ target }) => onChange((target as any).checked);

  return (
    <Checkbox
      label={props.label}
      defaultChecked={!!value}
      onChange={newOnChange}
      {...inputProps}
      className={classNames('form-checkbox', { 'form-checkbox--small': props.small }, getIntentClassName(props.meta))}
    />
  );
};
