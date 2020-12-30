import { FormGroup } from '@blueprintjs/core';
import { lazy, Suspense } from 'react';

import { getIntent } from '../meta';
import { FormInputValidation } from '../FormInputValidation/FormInputValidation';

export function FormRichTextArea({ rows, input, label, meta }) {
  const LazyTinyMCETextArea = lazy(() => import('./TinyMCETextArea'));

  return (
    <FormGroup labelFor={input.name} label={label} intent={getIntent(meta)}>
      <Suspense fallback={null}>
        <LazyTinyMCETextArea onChange={input.onChange} />
      </Suspense>
      <textarea rows={rows} {...input} className="tinymce" />
      <FormInputValidation meta={meta} />
    </FormGroup>
  );
}
