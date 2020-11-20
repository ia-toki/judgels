import { Classes } from '@blueprintjs/core';
import classNames from 'classnames';
import * as React from 'react';

import { getIntentClassName } from '../meta';
import { FormTableInput } from '../FormTableInput/FormTableInput';

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
