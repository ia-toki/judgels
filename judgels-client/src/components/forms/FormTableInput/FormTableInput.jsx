import { FormGroup } from '@blueprintjs/core';
import { Box } from '@blueprintjs/labs';
import classNames from 'classnames';

import { FormInputValidation } from '../FormInputValidation/FormInputValidation';
import { getIntent } from '../meta';

import './FormTableInput.scss';

export function FormTableInput({
  className,
  keyStyles,
  keyClassName,
  label,
  labelHelper,
  meta,
  inputHelper,
  children,
}) {
  return (
    <tr className="form-table-input">
      <td style={keyStyles} className={keyClassName}>
        <Box paddingTop={1}>
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
