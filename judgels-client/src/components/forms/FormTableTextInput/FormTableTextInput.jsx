import { Classes } from '@blueprintjs/core';
import classNames from 'classnames';

import { FormTableInput } from '../FormTableInput/FormTableInput';
import { getIntentClassName } from '../meta';

export function FormTableTextInput(props) {
  const { input, meta, disabled, type } = props;
  return (
    <FormTableInput {...props}>
      <input
        {...input}
        type={type || 'text'}
        disabled={disabled}
        className={classNames(Classes.INPUT, getIntentClassName(meta))}
      />
    </FormTableInput>
  );
}
