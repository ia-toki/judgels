import { FormGroup } from '@blueprintjs/core';
import * as React from 'react';
import Loadable from 'react-loadable';

import { getIntent } from '../meta';
import { FormInputProps } from '../props';
import { FormInputValidation } from '../FormInputValidation/FormInputValidation';

export interface FormRichTextAreaProps extends FormInputProps {
  rows: number;
}

export class FormRichTextArea extends React.PureComponent<FormRichTextAreaProps> {
  render() {
    const { rows, input, label, meta } = this.props;

    const LoadableTinyMCETextArea = Loadable({
      loader: () => import('./TinyMCETextArea'),
      loading: () => null,
    });

    return (
      <FormGroup labelFor={input.name} label={label} intent={getIntent(meta)}>
        <LoadableTinyMCETextArea onChange={input.onChange} />
        <textarea rows={rows} {...input} className="tinymce" />
        <FormInputValidation meta={meta} />
      </FormGroup>
    );
  }
}
