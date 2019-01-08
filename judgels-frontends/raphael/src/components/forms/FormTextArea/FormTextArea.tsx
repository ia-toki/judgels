import { Classes, FormGroup } from '@blueprintjs/core';
import * as classNames from 'classnames';
import * as React from 'react';

import { getIntent, getIntentClassName } from '../meta';
import { FormInputProps } from '../props';
import { FormInputValidation } from '../FormInputValidation/FormInputValidation';

import './FormTextArea.css';

export interface FormTextAreaProps extends FormInputProps {
  rows: number;
  isCode?: boolean;
}

export const FormTextArea = (props: FormTextAreaProps) => (
  <FormGroup
    labelFor={props.input.name}
    label={props.label}
    intent={getIntent(props.meta)}
    labelInfo={props.labelHelper}
  >
    <textarea
      {...props.input}
      className={classNames(Classes.INPUT, 'form-textarea', getIntentClassName(props.meta), {
        'form-textarea--code': props.isCode,
      })}
      rows={props.rows}
      autoFocus={props.autoFocus}
    />
    <FormInputValidation meta={props.meta} />
  </FormGroup>
);
