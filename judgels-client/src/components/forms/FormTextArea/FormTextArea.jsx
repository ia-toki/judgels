import { Classes, FormGroup } from '@blueprintjs/core';
import classNames from 'classnames';

import { FormInputValidation } from '../FormInputValidation/FormInputValidation';
import { getIntent, getIntentClassName } from '../meta';

import './FormTextArea.scss';

export function FormTextArea({ input, label, labelHelper, meta, autoFocus, rows, isCode }) {
  return (
    <FormGroup labelFor={input.name} label={label} intent={getIntent(meta)} labelInfo={labelHelper}>
      <textarea
        {...input}
        className={classNames(Classes.INPUT, 'form-textarea', getIntentClassName(meta), {
          'form-textarea--code': isCode,
        })}
        rows={rows}
        autoFocus={autoFocus}
      />
      <FormInputValidation meta={meta} />
    </FormGroup>
  );
}
