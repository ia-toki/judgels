<#-- @ftlvariable type="judgels.michael.template.TemplateView" -->

<#import "base/baseLayout.ftl" as base>
<#import "base/columnLayout.ftl" as column>
<#import "content/contentLayout.ftl" as content>
<#import "content/titleView.ftl" as title>

<#import "base/headerView.ftl" as header>

<#macro layout>
  <@base.layout title=vars.title>
    <@header.view name=vars.name/>
    <main class="container">
      <@column.layout>
        <@content.layout>
          <@title.view title=vars.title/>
          <#nested>
        </@content.layout>
      </@column.layout>
    </main>
  </@base.layout>
</#macro>
