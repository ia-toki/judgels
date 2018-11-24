import { HTMLSelect } from '@blueprintjs/core';
import * as classNames from 'classnames';
import * as React from 'react';

import { getIntentClassName } from '../meta';
import { FormTableInput, FormTableInputProps } from '../FormTableInput/FormTableInput';

import './FormTableSelect.css';

export const FormTableSelect = (props: FormTableInputProps) => {
  return (
    <FormTableInput {...props}>
      <HTMLSelect {...props.input} className={classNames(getIntentClassName(props.meta))}>
        {props.children}
      </HTMLSelect>
    </FormTableInput>
  );
};
