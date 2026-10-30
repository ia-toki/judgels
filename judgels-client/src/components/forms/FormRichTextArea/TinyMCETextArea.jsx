import { useEffect, useState } from 'react';

import { useWebPrefs } from '../../../modules/webPrefs';
import contentCssUrl from './TinyMCEContent.scss?url';

import './TinyMCETextArea.scss';

let isTinyMCEScriptAdded = false;

// Statements written in another editor hold markup that TinyMCE strips by default.
const permissiveOptions = {
  valid_elements: '*[*]',
  extended_valid_elements: 'iframe[*],embed[*]',
};

function TinyMCETextArea({ onChange, id, permissive, height, baseMediaURL }) {
  const [isLoaded, setIsLoaded] = useState(!!window.tinymce);
  const { isDarkMode } = useWebPrefs();

  useEffect(() => {
    if (window.tinymce) {
      setIsLoaded(true);
      return;
    }

    if (isTinyMCEScriptAdded) {
      const interval = setInterval(() => {
        if (window.tinymce) {
          setIsLoaded(true);
          clearInterval(interval);
        }
      }, 100);
      return () => clearInterval(interval);
    }

    isTinyMCEScriptAdded = true;

    const script = document.createElement('script');
    script.src = '/tinymce/tinymce.min.js';
    script.async = true;
    script.addEventListener('load', () => setIsLoaded(true));

    document.body.appendChild(script);

    return () => {
      script.removeEventListener('load', () => setIsLoaded(true));
    };
  }, []);

  useEffect(() => {
    if (!isLoaded) {
      return;
    }

    tinymce.init({
      selector: `#${id}`,
      skin_url: `${window.location.origin}/tinymce/skins/lightgray`,
      content_css: new URL(contentCssUrl, window.location.origin).href,
      body_class: 'html-text',
      visual: false,
      branding: false,
      menubar: false,
      plugins: 'code image link lists',
      toolbar:
        'bold italic underline strikethrough subscript superscript blockquote removeformat | ' +
        'bullist numlist | outdent indent | ' +
        'alignleft aligncenter alignright alignjustify | ' +
        'image link | ' +
        'styleselect formatselect | ' +
        'code',
      relative_urls: false,
      convert_urls: false,
      remove_script_host: false,
      ...(baseMediaURL ? { document_base_url: baseMediaURL } : {}),
      ...(height ? { height } : {}),
      ...(permissive ? permissiveOptions : {}),
      setup: editor => {
        editor.on('PreInit', () => {
          editor.getDoc().documentElement.classList.add(isDarkMode ? 'bp6-dark' : 'bp6-light');
        });
        editor.on('change', () => {
          onChange(editor.getContent());
        });
      },
    });

    return () => {
      const editor = tinymce.get(id);
      if (editor) {
        editor.remove();
      }
    };
  }, [isLoaded, id, isDarkMode]);

  return null;
}

export default TinyMCETextArea;
