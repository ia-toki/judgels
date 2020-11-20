import { FormGroup } from '@blueprintjs/core';
import classNames from 'classnames';
import * as React from 'react';

import { getIntent } from '../meta';
import { FormInputValidation } from '../FormInputValidation/FormInputValidation';

import './FormTableInput.css';

export function FormTableInput({ className, keyClassName, label, labelHelper, meta, inputHelper, children }) {
  return (
    <tr className="form-table-input">
      <td className={keyClassName}>
        <div className="form-table-input__group-label">
          <span className="form-table-input__label">{label}</span>
          {labelHelper && <span className="form-table-input__label-helper"> ({labelHelper})</span>}
        </div>
      </td>
      <td>
        <FormGroup
          intent={getIntent(meta)}
          helperText={inputHelper}
          className={classNames('form-table-input__group', className)}
        >
          {children}
          <FormInputValidation meta={meta} />
        </FormGroup>
      </td>
    </tr>
  );
}
