import { Tag } from '@blueprintjs/core';
import { DateInput, TimePrecision } from '@blueprintjs/datetime';
import classNames from 'classnames';
import * as React from 'react';

import { formatDateTime, formatDateTimezoneOffset, parseDateTime } from '../../../utils/datetime';

import { getIntentClassName } from '../meta';
import { FormInputProps } from '../props';
import { FormInputValidation } from '../FormInputValidation/FormInputValidation';
import { FormTableInput } from '../FormTableInput/FormTableInput';

import './FormTableDateInput.css';

export class FormTableDateInput extends React.PureComponent<FormInputProps> {
  render() {
    const { props } = this;
    const { onChange, ...inputProps } = props.input;
    return (
      <FormTableInput {...props}>
        <DateInput
          className={classNames(getIntentClassName(props.meta))}
          formatDate={this.formatDate}
          parseDate={parseDateTime}
          rightElement={<Tag>{formatDateTimezoneOffset(new Date())}</Tag>}
          timePrecision={TimePrecision.MINUTE}
          canClearSelection={false}
          closeOnSelection={false}
          maxDate={new Date(4102444800000)}
          timePickerProps={{ showArrowButtons: true }}
          onChange={onChange}
          inputProps={{ name: inputProps.name }}
          {...inputProps}
        />
        <FormInputValidation meta={props.meta} />
      </FormTableInput>
    );
  }

  private formatDate = (date: Date) => formatDateTime(date);
}
