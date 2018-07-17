import * as React from 'react';

import { FormInputMeta, isValid } from '../meta';

export interface FormInputValidationProps {
  meta: FormInputMeta;
}

export const FormInputValidation = (props: FormInputValidationProps) =>
  isValid(props.meta) ? null : <div className="form-text-input-error pt-form-helper-text">{props.meta.error}</div>;
