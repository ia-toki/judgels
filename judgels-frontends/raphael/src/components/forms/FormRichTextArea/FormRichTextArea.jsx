import { FormGroup } from '@blueprintjs/core';
import * as React from 'react';

import { getIntent } from '../meta';
import { FormInputValidation } from '../FormInputValidation/FormInputValidation';

export function FormRichTextArea({ rows, input, label, meta }) {
  const LazyTinyMCETextArea = React.lazy(() => import('./TinyMCETextArea'));

  return (
    <FormGroup labelFor={input.name} label={label} intent={getIntent(meta)}>
      <React.Suspense fallback={null}>
        <LazyTinyMCETextArea onChange={input.onChange} />
      </React.Suspense>
      <textarea rows={rows} {...input} className="tinymce" />
      <FormInputValidation meta={meta} />
    </FormGroup>
  );
}
