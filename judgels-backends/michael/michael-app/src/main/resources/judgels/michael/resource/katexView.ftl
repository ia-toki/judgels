<#macro view>
  <link rel="stylesheet" href="/webjars/katex/dist/katex.min.css"/>
  <script src="/webjars/katex/dist/katex.min.js"></script>
  <script src="/webjars/katex/dist/contrib/auto-render.min.js"></script>
  <script>
    document.addEventListener('DOMContentLoaded', function() {
      renderMathInElement(document.body, {
        delimiters: [
          { left: "\\(", right: "\\)", display: false },
          { left: "$", right: "$", display: false },
          { left: "\\[", right: "\\]", display: true },
        ]
      });
    }, false);
  </script>
</#macro>
