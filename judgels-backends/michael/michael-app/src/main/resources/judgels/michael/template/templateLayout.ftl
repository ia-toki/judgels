<#-- @ftlvariable type="judgels.michael.template.TemplateView" -->

<#import "base/baseLayout.ftl" as base>
<#import "base/columnLayout.ftl" as column>
<#import "content/contentLayout.ftl" as content>
<#import "content/mainTabsView.ftl" as mainTabs>
<#import "content/secondaryTabsView.ftl" as secondaryTabs>
<#import "content/titleView.ftl" as title>
<#import "form/globalFormErrorView.ftl" as globalFormError>

<#import "base/headerView.ftl" as header>

<#macro layout>
  <@base.layout title=vars.title>
    <@header.view name=vars.name username=vars.username avatarUrl=vars.avatarUrl/>
    <main class="container">
      <@column.layout
        sidebarMenus=vars.sidebarMenus
        activeSidebarMenu=vars.activeSidebarMenu
        searchProblemsWidget=vars.searchProblemsWidget
        hasSearchProblemsWidget=vars.hasSearchProblemsWidget
        searchLessonsWidget=vars.searchLessonsWidget
        hasSearchLessonsWidget=vars.hasSearchLessonsWidget
      >
        <@content.layout>
          <@title.view title=vars.title buttons=vars.mainButtons/>
          <@mainTabs.view tabs=vars.mainTabs activeTab=vars.activeMainTab/>
          <@secondaryTabs.view tabs=vars.secondaryTabs activeTab=vars.activeSecondaryTab/>
          <@globalFormError.view message=(form.globalError)!""/>
          <#nested>
        </@content.layout>
      </@column.layout>
    </main>
  </@base.layout>
</#macro>
