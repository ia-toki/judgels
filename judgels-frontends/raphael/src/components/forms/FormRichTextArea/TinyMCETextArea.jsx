import * as React from 'react';

import tinymce from 'tinymce/tinymce';
import 'tinymce/themes/modern/theme';
import 'tinymce/plugins/code';
import 'tinymce/plugins/image';
import 'tinymce/plugins/link';
import 'tinymce/plugins/lists';
import 'tinymce/plugins/paste';
import 'tinymce/plugins/table';

export class TinyMCETextArea extends React.PureComponent {
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
        relative_urls: false,
        convert_urls: false,
        remove_script_host: false,
        setup: editor => {
          editor.on('change', () => {
            this.props.onChange(editor.getContent());
          });
        },
      });
    }
  }

  render() {
    return null;
  }
}

export default TinyMCETextArea;
