import { Classes } from '@blueprintjs/core';
import * as classNames from 'classnames';
import * as React from 'react';

import { FormInputMeta, isValid } from '../meta';

export interface FormInputValidationProps {
  meta: FormInputMeta;
}

export const FormInputValidation = (props: FormInputValidationProps) =>
  isValid(props.meta) ? null : (
    <div className={classNames(Classes.FORM_HELPER_TEXT, 'form-text-input-error')}>{props.meta.error}</div>
  );
