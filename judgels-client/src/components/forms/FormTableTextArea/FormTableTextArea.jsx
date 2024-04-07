import { Classes } from '@blueprintjs/core';
import classNames from 'classnames';

import { FormTableInput } from '../FormTableInput/FormTableInput';
import { getIntentClassName } from '../meta';

import './FormTableTextArea.scss';

export function FormTableTextArea(props) {
  const { input, meta, isCode, rows } = props;
  return (
    <FormTableInput {...props}>
      <textarea
        {...input}
        className={classNames(Classes.INPUT, 'form-table-textarea', getIntentClassName(meta), {
          'form-table-textarea--code': isCode,
        })}
        rows={rows || 5}
      />
    </FormTableInput>
  );
}
