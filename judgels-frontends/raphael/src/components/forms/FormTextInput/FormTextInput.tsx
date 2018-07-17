import { FormGroup } from '@blueprintjs/core';
import * as classNames from 'classnames';
import * as React from 'react';

import { getIntent, getIntentClassName } from '../meta';
import { FormInputProps } from '../props';
import { FormInputValidation } from '../FormInputValidation/FormInputValidation';

export interface FormTextInputProps extends FormInputProps {
  type?: 'password';
}

export const FormTextInput = (props: FormTextInputProps) => (
  <FormGroup labelFor={props.input.name} label={props.label} intent={getIntent(props.meta)}>
    <input
      {...props.input}
      type={props.type || 'text'}
      className={classNames('pt-input', getIntentClassName(props.meta))}
    />
    <FormInputValidation meta={props.meta} />
  </FormGroup>
);
