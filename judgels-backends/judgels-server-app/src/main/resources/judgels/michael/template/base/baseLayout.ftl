<#macro layout title>
  <!DOCTYPE html>
  <html>
    <head>
      <meta charset="UTF-8">
      <meta name="robots" content="noindex">
      <title>${title}</title>
      <link rel="stylesheet" href="/assets/css/reset.css">
      <link rel="stylesheet" href="/webjars/open-sans/1.1.0/css/open-sans.min.css">
      <link rel="stylesheet" href="/webjars/roboto-fontface/0.7.0/css/roboto/roboto-fontface.css">
      <link rel="stylesheet" href="/webjars/bootstrap/3.3.4/css/bootstrap.min.css">
      <link rel="stylesheet" href="/assets/css/main-6.css">
      <link rel="shortcut icon" type="image/ico" href="/assets/images/favicon.ico">
      <script src="/webjars/jquery/2.1.4/jquery.min.js"></script>
      <script src="/webjars/bootstrap/3.3.4/js/bootstrap.min.js"></script>
    </head>
    <body>
      <#nested>
    </body>
  </html>
</#macro>
