<#-- @ftlvariable type="judgels.michael.template.TemplateView" -->

<#import "base/baseLayout.ftl" as base>

<#macro layout>
  <@base.layout title=vars.title>
    <#nested>
  </@base.layout>
</#macro>
