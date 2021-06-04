import { Checkbox } from '@blueprintjs/core';
import classNames from 'classnames';

import { getIntentClassName } from '../meta';

import './FormCheckbox.scss';

export function FormCheckbox({ input, label, small, meta }) {
  const { value, onChange, ...inputProps } = input;
  const newOnChange = ({ target }) => onChange(target.checked);

  return (
    <Checkbox
      label={label}
      defaultChecked={!!value}
      onChange={newOnChange}
      {...inputProps}
      className={classNames('form-checkbox', { 'form-checkbox--small': small }, getIntentClassName(meta))}
    />
  );
}
