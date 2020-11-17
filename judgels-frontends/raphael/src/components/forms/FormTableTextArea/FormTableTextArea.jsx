import { Classes } from '@blueprintjs/core';
import classNames from 'classnames';
import * as React from 'react';

import { getIntentClassName } from '../meta';
import { FormTableInput, FormTableInputProps } from '../FormTableInput/FormTableInput';

import './FormTableTextArea.css';

export const FormTableTextArea = (props: FormTableInputProps) => (
  <FormTableInput {...props}>
    <textarea
      {...props.input}
      className={classNames(Classes.INPUT, 'form-table-textarea', getIntentClassName(props.meta))}
      rows={5}
    />
  </FormTableInput>
);
