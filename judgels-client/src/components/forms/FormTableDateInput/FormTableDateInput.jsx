import { DateInput, TimePrecision } from '@blueprintjs/datetime';
import classNames from 'classnames';

import { formatDateTime, parseDateTime } from '../../../utils/datetime';

import { getIntentClassName } from '../meta';
import { FormInputValidation } from '../FormInputValidation/FormInputValidation';
import { FormTableInput } from '../FormTableInput/FormTableInput';

import './FormTableDateInput.scss';

export function FormTableDateInput(props) {
  const { input, meta } = props;

  const formatDate = date => formatDateTime(date);

  const { onChange, ...inputProps } = input;
  return (
    <FormTableInput {...props}>
      <DateInput
        className={classNames('form-table-date-input', getIntentClassName(meta))}
        formatDate={formatDate}
        parseDate={parseDateTime}
        timePrecision={TimePrecision.MINUTE}
        canClearSelection={false}
        closeOnSelection={false}
        maxDate={new Date(4102444800000)}
        timePickerProps={{ showArrowButtons: true }}
        onChange={onChange}
        inputProps={{ name: inputProps.name }}
        {...inputProps}
      />
      <FormInputValidation meta={meta} />
    </FormTableInput>
  );
}
