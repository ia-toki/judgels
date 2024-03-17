<#macro layout title>
  <!DOCTYPE html>
  <html>
    <head>
      <meta charset="UTF-8">
      <meta name="robots" content="noindex">
      <title>${title}</title>
      <link rel="stylesheet" href="/assets/css/reset.css">
      <link rel="stylesheet" href="/webjars/open-sans/css/open-sans.min.css">
      <link rel="stylesheet" href="/webjars/roboto-fontface/css/roboto/roboto-fontface.css">
      <link rel="stylesheet" href="/webjars/bootstrap/css/bootstrap.min.css">
      <link rel="stylesheet" href="/assets/css/main-2.css">
      <link rel="shortcut icon" type="image/ico" href="/assets/images/favicon.ico">
      <script src="/webjars/jquery/jquery.min.js"></script>
      <script src="/webjars/bootstrap/js/bootstrap.min.js"></script>
    </head>
    <body>
      <#nested>
    </body>
  </html>
</#macro>
