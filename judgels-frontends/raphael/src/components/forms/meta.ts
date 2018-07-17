import { Intent } from '@blueprintjs/core';

export interface FormInputMeta {
  touched: boolean;
  valid: boolean;
  error?: string;
}

export function isValid(meta: FormInputMeta) {
  return !meta.touched || meta.valid;
}

export function getIntent(meta: FormInputMeta) {
  return isValid(meta) ? undefined : Intent.DANGER;
}

export function getIntentClassName(meta: FormInputMeta) {
  return {
    'pt-intent-danger': !isValid(meta),
  };
}
