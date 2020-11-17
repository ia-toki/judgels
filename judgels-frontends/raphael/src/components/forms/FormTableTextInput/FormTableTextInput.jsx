import { Classes } from '@blueprintjs/core';
import classNames from 'classnames';
import * as React from 'react';

import { getIntentClassName } from '../meta';
import { FormTableInput, FormTableInputProps } from '../FormTableInput/FormTableInput';

export interface FormTableTextInputProps extends FormTableInputProps {
  type?: 'password';
}

export const FormTableTextInput = (props: FormTableTextInputProps) => (
  <FormTableInput {...props}>
    <input
      {...props.input}
      type={props.type || 'text'}
      disabled={props.disabled}
      className={classNames(Classes.INPUT, getIntentClassName(props.meta))}
    />
  </FormTableInput>
);
