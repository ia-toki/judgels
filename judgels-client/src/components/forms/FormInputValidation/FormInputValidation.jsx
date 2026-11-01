import { Classes } from '@blueprintjs/core';

import { isValid } from '../meta';

export function FormInputValidation({ meta }) {
  return isValid(meta) ? null : <div className={Classes.FORM_HELPER_TEXT}>{meta.error || meta.submitError}</div>;
}
