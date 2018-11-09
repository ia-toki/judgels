import { FormGroup } from '@blueprintjs/core';
import * as React from 'react';

import { getIntent } from '../meta';
import { FormInputProps } from '../props';
import { FormInputValidation } from '../FormInputValidation/FormInputValidation';

import tinymce from 'tinymce/tinymce';
import 'tinymce/themes/modern/theme';
import 'tinymce/plugins/code';
import 'tinymce/plugins/image';
import 'tinymce/plugins/link';
import 'tinymce/plugins/lists';
import 'tinymce/plugins/paste';
import 'tinymce/plugins/table';

export interface FormRichTextAreaProps extends FormInputProps {
  rows: number;
}

export class FormRichTextArea extends React.PureComponent<FormRichTextAreaProps> {
  componentDidMount() {
    if (tinymce) {
      tinymce.init({
        selector: '.tinymce',
        skin_url: '/skins/lightgray',
        content_css: '/skins/raphael/content.css',
        branding: false,
        menubar: false,
        plugins: 'code image link lists table',
        toolbar:
          'bold italic underline strikethrough subscript superscript blockquote removeformat | ' +
          'bullist numlist | outdent indent | ' +
          'alignleft aligncenter alignright alignjustify | ' +
          'image link table | ' +
          'styleselect formatselect | ' +
          'code',
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
