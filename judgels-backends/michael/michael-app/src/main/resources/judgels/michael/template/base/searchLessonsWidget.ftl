<#import "/judgels/michael/template/form/forms.ftl" as forms>

<#macro widget data>
  <div class="widget clearfix">
    <div class="col-md-12">
      <form method="GET" action="/lessons">
        <div class="form-group">
          <label>Slug / additional note</label>
          <input type="search" class="form-control" name="filter" value="${data.filterString}">
        </div>

        <@forms.submit>Search</@forms.submit>
      </form>
    </div>
  </div>
</#macro>
