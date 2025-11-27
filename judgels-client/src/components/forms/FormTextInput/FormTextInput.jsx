import { Classes, FormGroup } from '@blueprintjs/core';
import classNames from 'classnames';

import { FormInputValidation } from '../FormInputValidation/FormInputValidation';
import { getIntent, getIntentClassName } from '../meta';

export function FormTextInput({ input, label, meta, autoFocus, inputType }) {
  return (
    <FormGroup labelFor={input.name} label={label} intent={getIntent(meta)}>
      <input
        {...input}
        id={input.name}
        type={inputType || 'text'}
        autoFocus={autoFocus}
        className={classNames(Classes.INPUT, getIntentClassName(meta))}
      />
      <FormInputValidation meta={meta} />
    </FormGroup>
  );
}
