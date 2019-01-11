requirejs.config({
    shim: {
        'ckeditor-jquery': {
            deps: ['jquery', 'ckeditor-core']
        }
    },
    paths: {
        'ckeditor-core': '/assets/lib/ckeditor/full/ckeditor',
        'ckeditor-jquery': '/assets/lib/ckeditor/full/adapters/jquery'
    }
});

require(["jquery", "ckeditor-jquery"], function( __jquery__ ) {
    CKEDITOR.config.toolbar = [
        ['Bold', 'Italic', 'Underline', 'Strike', 'Subscript', 'Superscript', '-', 'RemoveFormat'], ['NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'Blockquote'], ['JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock'], ['Image', 'Link', 'Table'], ['Styles', 'Format'], ['Source', '-', 'Preview']
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
    CKEDITOR.config.extraAllowedContent = 'iframe embed';
    CKEDITOR.config.disallowedContent = 'script; *[on*]';
    $('.ckeditor').ckeditor();
});