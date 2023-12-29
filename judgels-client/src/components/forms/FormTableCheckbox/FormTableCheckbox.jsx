import { Checkbox } from '@blueprintjs/core';
import classNames from 'classnames';

import { FormTableInput } from '../FormTableInput/FormTableInput';
import { getIntentClassName } from '../meta';

import './FormTableCheckbox.scss';

export function FormTableCheckbox(props) {
  const { input, meta } = props;
  const { value, onChange, ...inputProps } = input;
  const newOnChange = ({ target }) => onChange(target.checked);

  return (
    <FormTableInput {...props}>
      <Checkbox
        defaultChecked={!!value}
        onChange={newOnChange}
        {...inputProps}
        className={classNames(getIntentClassName(meta))}
      />
    </FormTableInput>
  );
}
