import { Classes } from '@blueprintjs/core';
import classNames from 'classnames';

import { getIntentClassName } from '../meta';
import { FormTableInput } from '../FormTableInput/FormTableInput';

import './FormTableTextArea.css';

export function FormTableTextArea(props) {
  const { input, meta } = props;
  return (
    <FormTableInput {...props}>
      <textarea
        {...input}
        className={classNames(Classes.INPUT, 'form-table-textarea', getIntentClassName(meta))}
        rows={5}
      />
    </FormTableInput>
  );
}
