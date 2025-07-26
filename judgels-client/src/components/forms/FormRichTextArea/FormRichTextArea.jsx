import { FormGroup } from '@blueprintjs/core';

import { FormInputValidation } from '../FormInputValidation/FormInputValidation';
import { getIntent } from '../meta';

export function FormRichTextArea({ rows, input, label, meta }) {
  return (
    <FormGroup labelFor={input.name} label={label} intent={getIntent(meta)}>
      <textarea rows={rows} {...input} />
      <FormInputValidation meta={meta} />
    </FormGroup>
  );
}
