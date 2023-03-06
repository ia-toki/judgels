<#macro view>
  <script src="/webjars/ckeditor/full/ckeditor.js"></script>
  <script src="/webjars/ckeditor/full/adapters/jquery.js"></script>
  <script>
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
      $('.ckeditor').ckeditor();
    }, false);
  </script>
</#macro>
