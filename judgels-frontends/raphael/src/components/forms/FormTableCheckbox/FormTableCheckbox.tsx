import { Checkbox } from '@blueprintjs/core';
import * as classNames from 'classnames';
import * as React from 'react';

import { getIntentClassName } from '../meta';
import { FormTableInput, FormTableInputProps } from '../FormTableInput/FormTableInput';

import './FormTableCheckbox.css';

export const FormTableCheckbox = (props: FormTableInputProps) => {
  const { value, ...inputProps } = props.input;
  return (
    <FormTableInput {...props}>
      <Checkbox defaultChecked={!!value} {...inputProps} className={classNames(getIntentClassName(props.meta))} />
    </FormTableInput>
  );
};
