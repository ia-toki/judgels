import * as classNames from 'classnames';
import * as React from 'react';

import { getIntentClassName } from '../meta';
import { FormTableInput, FormTableInputProps } from '../FormTableInput/FormTableInput';

import './FormTableSelect.css';

export const FormTableSelect = (props: FormTableInputProps) => {
  return (
    <FormTableInput {...props}>
      <div className="pt-select">
        <select {...props.input} className={classNames(getIntentClassName(props.meta))}>
          {props.children}
        </select>
      </div>
    </FormTableInput>
  );
};
