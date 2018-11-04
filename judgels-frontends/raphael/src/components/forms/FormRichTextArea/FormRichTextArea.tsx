import { FormGroup } from '@blueprintjs/core';
import * as React from 'react';

import { getIntent } from '../meta';
import { FormInputProps } from '../props';
import { FormInputValidation } from '../FormInputValidation/FormInputValidation';

import tinymce from 'tinymce/tinymce';
import 'tinymce/themes/modern/theme';
import 'tinymce/plugins/paste';
import 'tinymce/plugins/link';

export interface FormRichTextAreaProps extends FormInputProps {
  rows: number;
}

export class FormRichTextArea extends React.PureComponent<FormRichTextAreaProps> {
  componentDidMount() {
    if (tinymce) {
      tinymce.init({
        selector: '.tinymce',
        skin_url: '/skins/lightgray',
        branding: false,
        menubar: 'edit view format',
        setup: editor => {
          editor.on('change', () => {
            this.props.input.onChange(editor.getContent());
          });
        },
      });
    }
  }

  render() {
    const { rows, input, label, meta } = this.props;

    return (
      <FormGroup labelFor={input.name} label={label} intent={getIntent(meta)}>
        <textarea {...input} className="tinymce" rows={rows} />
        <FormInputValidation meta={meta} />
      </FormGroup>
    );
  }
}
