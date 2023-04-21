<#import "/judgels/michael/forms.ftl" as forms>
<#import "/judgels/michael/ui.ftl" as ui>

<#macro widget data>
  <div class="widget">
    <form method="GET" action="/lessons">
      <@forms.formGroup>
        <label>Slug / additional note</label>
        <input type="search" class="form-control" name="term" value="${data.termFilter}">
      </@forms.formGroup>

      <div class="text-center">
        <@ui.button type="submit">Search</@ui.button>
      </div>
    </form>
  </div>
</#macro>
