import { FormGroup } from '@blueprintjs/core';
import * as React from 'react';

import { getIntent } from '../meta';
import { FormInputProps } from '../props';
import { FormInputValidation } from '../FormInputValidation/FormInputValidation';

export interface FormRichTextAreaProps extends FormInputProps {
  rows: number;
}

export class FormRichTextArea extends React.PureComponent<FormRichTextAreaProps> {
  render() {
    const { rows, input, label, meta } = this.props;

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
}
