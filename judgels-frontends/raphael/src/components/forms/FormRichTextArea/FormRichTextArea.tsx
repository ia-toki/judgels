import { FormGroup } from '@blueprintjs/core';
import * as React from 'react';

import { getIntent } from '../meta';
import { FormInputProps } from '../props';
import { FormInputValidation } from '../FormInputValidation/FormInputValidation';

import tinymce from 'tinymce/tinymce';
import 'tinymce/themes/modern/theme';
import 'tinymce/plugins/paste';
import 'tinymce/plugins/link';

export const FormRichTextArea = (props: FormInputProps) => {
  tinymce.init({
    selector: '.tinymce',
    skin_url: '/skins/lightgray',
    branding: false,
    menubar: 'edit view format',
    setup: editor => {
      editor.on('change', () => {
        props.input.onChange(editor.getContent());
      });
    },
  });

  return (
    <FormGroup labelFor={props.input.name} label={props.label} intent={getIntent(props.meta)}>
      <textarea {...props.input} className="tinymce" />
      <FormInputValidation meta={props.meta} />
    </FormGroup>
  );
};
