<#macro layout title>
  <!DOCTYPE html>
  <html>
    <head>
      <meta name="robots" content="noindex">
      <title>${title}</title>
      <link rel="stylesheet" href="/assets/css/reset.css">
      <link rel="stylesheet" href="/webjars/bootstrap/css/bootstrap.min.css">
    </head>
    <body>
      <div class="container-fluid">
        <#nested>
      </div>
    </body>
  </html>
</#macro>
