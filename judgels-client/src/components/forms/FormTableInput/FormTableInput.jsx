import { FormGroup } from '@blueprintjs/core';
import { Box } from '@blueprintjs/labs';
import classNames from 'classnames';

import { FormInputValidation } from '../FormInputValidation/FormInputValidation';
import { getIntent } from '../meta';

import './FormTableInput.scss';

export function FormTableInput({ className, fixedKey, label, labelHelper, meta, inputHelper, children }) {
  return (
    <tr className="form-table-input">
      <td className={classNames({ 'col-key': fixedKey })}>
        <Box className="form-table-input__key">
          <span className="form-table-input__label">{label}</span>
          {labelHelper && <span className="form-table-input__label-helper"> ({labelHelper})</span>}
        </Box>
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
