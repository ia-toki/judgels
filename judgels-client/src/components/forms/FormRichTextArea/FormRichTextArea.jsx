import { FormGroup } from '@blueprintjs/core';
import { Suspense, lazy } from 'react';

import { lazyRetry } from '../../../lazy';
import { FormInputValidation } from '../FormInputValidation/FormInputValidation';
import { getIntent } from '../meta';

// A tall editor is as tall as a tall FormAceEditor: TinyMCE's height excludes its toolbars.
const TALL_HEIGHT = 600;

export function FormRichTextArea({ rows, input, label, meta, permissive, tall }) {
  const LazyTinyMCETextArea = lazy(() => lazyRetry(() => import('./TinyMCETextArea')));

  return (
    <FormGroup labelFor={input.name} label={label} intent={getIntent(meta)}>
      <Suspense fallback={null}>
        <LazyTinyMCETextArea
          onChange={input.onChange}
          id={input.name}
          permissive={permissive}
          height={tall ? TALL_HEIGHT : undefined}
        />
      </Suspense>
      <textarea id={input.name} rows={rows} {...input} className="tinymce" />
      <FormInputValidation meta={meta} />
    </FormGroup>
  );
}
