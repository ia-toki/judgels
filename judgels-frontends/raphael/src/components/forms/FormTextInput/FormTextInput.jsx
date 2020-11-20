import { Classes, FormGroup } from '@blueprintjs/core';
import classNames from 'classnames';
import * as React from 'react';

import { getIntent, getIntentClassName } from '../meta';
import { FormInputValidation } from '../FormInputValidation/FormInputValidation';

export function FormTextInput({ input, label, meta, autoFocus, type }) {
  return (
    <FormGroup labelFor={input.name} label={label} intent={getIntent(meta)}>
      <input
        {...input}
        type={type || 'text'}
        autoFocus={autoFocus}
        className={classNames(Classes.INPUT, getIntentClassName(meta))}
      />
      <FormInputValidation meta={meta} />
    </FormGroup>
  );
}
