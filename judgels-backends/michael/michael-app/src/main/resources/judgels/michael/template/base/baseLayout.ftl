<#macro layout title>
  <!DOCTYPE html>
  <html>
    <head>
      <meta name="robots" content="noindex">
      <title>${title}</title>
      <link rel="stylesheet" href="/assets/css/reset.css">
      <link rel="stylesheet" href="/webjars/open-sans/css/open-sans.min.css">
      <link rel="stylesheet" href="/webjars/roboto-fontface/css/roboto/roboto-fontface.css">
      <link rel="stylesheet" href="/webjars/bootstrap/css/bootstrap.min.css">
      <link rel="stylesheet" href="/assets/css/main.css">
    </head>
    <body>
      <div class="container-fluid">
        <#nested>
      </div>
    </body>
  </html>
</#macro>
