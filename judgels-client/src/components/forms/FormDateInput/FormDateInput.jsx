import { FormGroup } from '@blueprintjs/core';
import { DateInput, TimePrecision } from '@blueprintjs/datetime';
import classNames from 'classnames';

import { formatDateTime, parseDateTime } from '../../../utils/datetime';
import { FormInputValidation } from '../FormInputValidation/FormInputValidation';
import { getIntent, getIntentClassName } from '../meta';

import './FormDateInput.scss';

export function FormDateInput({ input, className, label, meta }) {
  const { onChange, ...inputProps } = input;

  return (
    <FormGroup className={className} label={label} intent={getIntent(meta)}>
      <DateInput
        className={classNames('form-date-input', getIntentClassName(meta))}
        formatDate={formatDateTime}
        parseDate={parseDateTime}
        timePrecision={TimePrecision.MINUTE}
        canClearSelection={false}
        closeOnSelection={false}
        maxDate={new Date(4102444800000)}
        minDate={new Date(0)}
        timePickerProps={{ showArrowButtons: true }}
        onChange={onChange}
        inputProps={{ name: inputProps.name }}
        {...inputProps}
      />
      <FormInputValidation meta={meta} />
    </FormGroup>
  );
}
