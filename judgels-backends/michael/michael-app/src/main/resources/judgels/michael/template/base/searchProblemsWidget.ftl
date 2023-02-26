<#macro widget data>
  <div class="widget clearfix">
    <div class="col-md-12">
      <form method="GET" action="/problems">
        <div class="form-group">
          <label>Slug / additional note</label>
          <input type="search" class="form-control" name="filter" value="${data.filterString}">
        </div>
      </form>
    </div>
  </div>
</#macro>
