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
        'jquery': '/assets/lib/jquery/jquery.min',
        'jquery-timer': '/assets/lib/playcommons/javascripts/jquery.timer',
        'jquery-ui': '/assets/lib/jquery-ui/jquery-ui.min',
        'prettify': '/assets/lib/prettify/prettify',
        'moment': '/assets/lib/momentjs/min/moment-with-locales.min',
        'select2': '/assets/lib/select2/js/select2.min'
    }
});
