<#-- @ftlvariable type="judgels.michael.template.TemplateView" -->

<#import "base/baseLayout.ftl" as base>
<#import "base/columnLayout.ftl" as column>
<#import "content/contentLayout.ftl" as content>
<#import "content/titleView.ftl" as title>
<#import "form/globalFormErrorView.ftl" as globalFormError>

<#import "base/headerView.ftl" as header>

<#macro layout>
  <@base.layout title=vars.title>
    <@header.view name=vars.name username=vars.username avatarUrl=vars.avatarUrl/>
    <main class="container">
      <@column.layout sidebarMenus=vars.sidebarMenus>
        <@content.layout>
          <@title.view title=vars.title/>
          <@globalFormError.view message=vars.globalFormErrorMessage/>
          <#nested>
        </@content.layout>
      </@column.layout>
    </main>
  </@base.layout>
</#macro>
