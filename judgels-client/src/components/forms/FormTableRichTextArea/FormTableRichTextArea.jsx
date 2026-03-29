import { Suspense, lazy } from 'react';

import { lazyRetry } from '../../../lazy';
import { FormTableInput } from '../FormTableInput/FormTableInput';

import './FormTableRichTextArea.scss';

export function FormTableRichTextArea(props) {
  const { input, rows } = props;
  const LazyTinyMCETextArea = lazy(() => lazyRetry(() => import('../FormRichTextArea/TinyMCETextArea')));

  return (
    <FormTableInput {...props}>
      <Suspense fallback={null}>
        <LazyTinyMCETextArea onChange={input.onChange} />
      </Suspense>
      <textarea id={input.name} rows={rows} {...input} className="tinymce form-table-rich-textarea" />
    </FormTableInput>
  );
}
