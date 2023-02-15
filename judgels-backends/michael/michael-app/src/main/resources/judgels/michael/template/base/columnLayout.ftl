<#macro layout singleColumn>
  <div class="row content">
    <#if singleColumn>
      <div class="row content">
        <div class="col-md-offset-3 col-md-6">
          <#nested>
        </div>
      </div>
    <#else>
      <div class="col-md-3">
        <div class="sidebar clearfix">
          sidebar here
        </div>
      </div>
      <div class="col-md-9">
        <#nested>
      </div>
    </#if>
  </div>
</#macro>
