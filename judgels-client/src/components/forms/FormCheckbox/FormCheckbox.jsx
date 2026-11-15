import { Checkbox } from '@blueprintjs/core';
import classNames from 'classnames';

import { getIntentClassName } from '../meta';

export function FormCheckbox({ input, label, meta }) {
  const { value, onChange, ...inputProps } = input;
  const newOnChange = ({ target }) => onChange(target.checked);

  return (
    <Checkbox
      label={label}
      defaultChecked={!!value}
      onChange={newOnChange}
      {...inputProps}
      className={classNames(getIntentClassName(meta))}
    />
  );
}
