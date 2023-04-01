<#import "sidebarView.ftl" as sidebar>
<#import "searchProblemsWidget.ftl" as searchProblems>
<#import "searchLessonsWidget.ftl" as searchLessons>

<#macro
  layout
  sidebarMenus activeSidebarMenu
  searchProblemsWidget searchLessonsWidget
>
  <div class="row content">
    <#if sidebarMenus?size == 0>
      <div class="col-md-offset-3 col-md-6">
        <#nested>
      </div>
    <#else>
      <div class="col-md-3">
        <@sidebar.view menus=sidebarMenus activeMenu=activeSidebarMenu/>
        <#if searchProblemsWidget.isPresent()>
          <@searchProblems.widget data=searchProblemsWidget.get()/>
        </#if>
        <#if searchLessonsWidget.isPresent()>
          <@searchLessons.widget data=searchLessonsWidget.get()/>
        </#if>
      </div>
      <div class="col-md-9">
        <#nested>
      </div>
    </#if>
  </div>
</#macro>
