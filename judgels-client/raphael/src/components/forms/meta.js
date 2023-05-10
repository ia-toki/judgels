import { Intent } from '@blueprintjs/core';

export function isValid(meta) {
  return !meta.touched || meta.valid;
}

export function getIntent(meta) {
  return isValid(meta) ? undefined : Intent.DANGER;
}

export function getIntentClassName(meta) {
  return {
    'pt-intent-danger': !isValid(meta),
  };
}
