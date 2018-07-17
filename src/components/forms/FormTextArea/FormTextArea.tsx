import { FormGroup } from '@blueprintjs/core';
import * as classNames from 'classnames';
import * as React from 'react';

import { getIntent, getIntentClassName } from '../meta';
import { FormInputProps } from '../props';
import { FormInputValidation } from '../FormInputValidation/FormInputValidation';

import './FormTextArea.css';

export const FormTextArea = (props: FormInputProps) => (
  <FormGroup labelFor={props.input.name} label={props.label} intent={getIntent(props.meta)}>
    <textarea
      {...props.input}
      className={classNames('pt-input', 'form-table-textarea', getIntentClassName(props.meta))}
      rows={5}
    />
    <FormInputValidation meta={props.meta} />
  </FormGroup>
);
