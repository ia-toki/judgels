import { useEffect, useState } from 'react';

let isTinyMCEScriptAdded = false;

function TinyMCETextArea({ onChange }) {
  const [isLoaded, setIsLoaded] = useState(!!window.tinymce);

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
      selector: '.tinymce',
      skin_url: '/tinymce/skins/lightgray',
      content_css: '/skins/judgels/content.css',
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
      setup: editor => {
        editor.on('change', () => {
          onChange(editor.getContent());
        });
      },
    });
  });

  return null;
}

export default TinyMCETextArea;
