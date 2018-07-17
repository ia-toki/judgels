import * as classNames from 'classnames';
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
      className={classNames('pt-input', getIntentClassName(props.meta))}
    />
  </FormTableInput>
);
