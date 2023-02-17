<#import "sidebarView.ftl" as sidebar>

<#macro layout sidebarMenus>
  <div class="row content">
    <#if sidebarMenus?size == 0>
      <div class="col-md-offset-3 col-md-6">
        <#nested>
      </div>
    <#else>
      <div class="col-md-3">
        <@sidebar.view menus=sidebarMenus/>
      </div>
      <div class="col-md-9">
        <#nested>
      </div>
    </#if>
  </div>
</#macro>
