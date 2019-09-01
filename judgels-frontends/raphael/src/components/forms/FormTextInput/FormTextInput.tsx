import { Classes, FormGroup } from '@blueprintjs/core';
import classNames from 'classnames';
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
      autoFocus={props.autoFocus}
      className={classNames(Classes.INPUT, getIntentClassName(props.meta))}
    />
    <FormInputValidation meta={props.meta} />
  </FormGroup>
);
