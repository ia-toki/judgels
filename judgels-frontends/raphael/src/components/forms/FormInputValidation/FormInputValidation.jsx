import { Classes } from '@blueprintjs/core';
import classNames from 'classnames';
import * as React from 'react';

import { isValid } from '../meta';

export function FormInputValidation({ meta }) {
  return isValid(meta) ? null : (
    <div className={classNames(Classes.FORM_HELPER_TEXT, 'form-text-input-error')}>{meta.error}</div>
  );
}
