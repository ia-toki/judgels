import { Classes, FormGroup } from '@blueprintjs/core';
import classNames from 'classnames';

import { getIntent, getIntentClassName } from '../meta';
import { FormInputValidation } from '../FormInputValidation/FormInputValidation';

export function FormTextInput({ input, label, meta, autoFocus, inputType }) {
  return (
    <FormGroup labelFor={input.name} label={label} intent={getIntent(meta)}>
      <input
        {...input}
        type={inputType || 'text'}
        autoFocus={autoFocus}
        className={classNames(Classes.INPUT, getIntentClassName(meta))}
      />
      <FormInputValidation meta={meta} />
    </FormGroup>
  );
}
