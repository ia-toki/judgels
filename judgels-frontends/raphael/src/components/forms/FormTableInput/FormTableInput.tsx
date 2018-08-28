import { FormGroup } from '@blueprintjs/core';
import * as classNames from 'classnames';
import * as React from 'react';

import { getIntent } from '../meta';
import { FormInputProps } from '../props';
import { FormInputValidation } from '../FormInputValidation/FormInputValidation';

import './FormTableInput.css';

export interface FormTableInputProps extends FormInputProps {
  children?: any;
}

export const FormTableInput = (props: FormTableInputProps) => (
  <tr className="form-table-input">
    <td>
      <div className="form-table-input__group-label">
        <span className="form-table-input__label">{props.label}</span>
        {props.labelHelper && <span className="form-table-input__label-helper"> ({props.labelHelper})</span>}
      </div>
    </td>
    <td>
      <FormGroup
        intent={getIntent(props.meta)}
        helperText={props.inputHelper}
        className={classNames('form-table-input__group', props.className)}
      >
        {props.children}
        <FormInputValidation meta={props.meta} />
      </FormGroup>
    </td>
  </tr>
);
