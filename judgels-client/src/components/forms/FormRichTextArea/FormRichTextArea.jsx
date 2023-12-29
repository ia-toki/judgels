import { FormGroup } from '@blueprintjs/core';
import { Suspense, lazy } from 'react';

import { lazyRetry } from '../../../lazy';
import { FormInputValidation } from '../FormInputValidation/FormInputValidation';
import { getIntent } from '../meta';

export function FormRichTextArea({ rows, input, label, meta }) {
  const LazyTinyMCETextArea = lazy(() => lazyRetry(() => import('./TinyMCETextArea')));

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
