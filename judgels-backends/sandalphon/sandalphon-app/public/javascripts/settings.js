requirejs.config({
    shim: {
        'bootstrap': {
            deps: ['jquery']
        },
        'bootstrap-datetimepicker': {
            deps: ['bootstrap', 'moment']
        },
        'jquery-history': {
            deps: ['jquery']
        },
        'jquery-placeholder': {
            deps: ['jquery']
        },
        'jquery-timer': {
            deps: ['jquery']
        },
        'jquery-ui': {
            deps: ['jquery']
        },
        'select2': {
            deps: ['jquery']
        }
    },
    paths: {
        'bootstrap': '/assets/lib/bootstrap/js/bootstrap.min',
        'bootstrap-datetimepicker': '/assets/lib/Eonasdan-bootstrap-datetimepicker/bootstrap-datetimepicker.min',
        'katex': '/assets/lib/katex/dist/katex.min',
        'katex-contrib-auto-render': '/assets/lib/katex/dist/contrib/auto-render.min',
        'jquery': '/assets/lib/jquery/jquery.min',
        'jquery-timer': '/assets/javascripts/jquery.timer',
        'jquery-ui': '/assets/lib/jquery-ui/jquery-ui.min',
        'moment': '/assets/lib/momentjs/min/moment-with-locales.min',
        'select2': '/assets/lib/select2/js/select2.min'
    }
});
