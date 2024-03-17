document.addEventListener('DOMContentLoaded', function() {
  CKEDITOR.config.toolbar = [
    ['Bold', 'Italic', 'Underline', 'Strike', 'Subscript', 'Superscript', '-', 'RemoveFormat'],
    ['NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'Blockquote'],
    ['JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock'],
    ['Image', 'Link', 'Table'],
    ['Styles', 'Format'],
    ['Source', '-', 'Preview']
  ];
  CKEDITOR.config.allowedContent = {
    $1: {
      // Use the ability to specify elements as an object.
      elements: CKEDITOR.dtd,
      attributes: true,
      styles: true,
      classes: true
    }
  };
  CKEDITOR.config.height = 600;
  CKEDITOR.config.extraAllowedContent = 'iframe embed';
  CKEDITOR.config.disallowedContent = 'script; *[on*]';
  CKEDITOR.config.removePlugins = 'exportpdf';
  CKEDITOR.addCss(`
    .cke_editable {
      font-size: 14px;
      color: #1c2127;
    }

    h3 {
      font-family: 'Roboto', sans-serif;
      font-size: 17px;
      font-weight: bold;
      margin-top: 20px;
      margin-bottom: 10px;
    }

    h4 {
      font-family: 'Roboto', sans-serif;
      font-size: 15px;
      font-weight: bold;
      margin-top: 10px;
      margin-bottom: 10px;
    }

    pre {
      padding: 9.5px;
      margin: 0 0 10px;
      border: 1px solid #ccc;
      border-radius: 4px;
    }

    pre, code {
      background-color: #f5f5f5;
    }

    code {
      padding: 2px 4px;
      border-radius: 4px;
      font-size: 90%;
    }

    ul, ol {
      padding-left: 25px;
    }

    table {
      border-collapse: collapse;
    }

    th {
      font-weight: bold;
      background-color: #eeeeee;
      text-align: left;
    }

    th, td {
      padding: 5px;
    }

    blockquote {
      padding: 10px 20px;
      margin: 0 0 20px;
      border-left: 5px solid #eee;
      font-style: normal;
    }
  `);
}, false);
