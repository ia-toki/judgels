<#import "sidebarView.ftl" as sidebar>
<#import "searchProblemsWidget.ftl" as searchProblems>

<#macro layout sidebarMenus activeSidebarMenu searchProblemsWidget hasSearchProblemsWidget>
  <div class="row content">
    <#if sidebarMenus?size == 0>
      <div class="col-md-offset-3 col-md-6">
        <#nested>
      </div>
    <#else>
      <div class="col-md-3">
        <@sidebar.view menus=sidebarMenus activeMenu=activeSidebarMenu/>
        <#if hasSearchProblemsWidget>
          <@searchProblems.widget data=searchProblemsWidget/>
        </#if>
      </div>
      <div class="col-md-9">
        <#nested>
      </div>
    </#if>
  </div>
</#macro>
