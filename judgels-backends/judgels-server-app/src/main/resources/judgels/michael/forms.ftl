<#import "/judgels/michael/ui.ftl" as ui>

<#macro form
  method="POST"
  type="horizontal"
  compact=false
  action=""
  multipart=false
  labelWidth=3
  fieldWidth=9
>
  <form
    method="${method}"
    class="form-${type}"
    <#if action?has_content>action="${action}"</#if>
    <#if multipart>enctype="multipart/form-data"</#if>
  >
    <#global globalFormType = type>
    <#global globalFormCompact = type == "inline" || compact>
    <#global globalFormValues = formValues!{}>
    <#global globalLabelWidth = labelWidth>
    <#global globalFieldWidth = fieldWidth>

    <#nested>
  </form>
</#macro>

<#function fieldInputClass>
  <#if !globalFormCompact>
    <#return "form-control">
  <#else>
    <#return "">
  </#if>
</#function>

<#macro formGroup>
  <div class="form-group">
    <#nested>
  </div>
</#macro>

<#macro formLabel value for="">
  <label
    class="<#if !globalFormCompact>control-label</#if> <#if globalFormType == "horizontal">col-md-${globalLabelWidth}</#if>"
    for="${for}"
  >
    <#if globalFormType == "inline">
      <small>${value}</small>
    <#else>
      ${value}
    </#if>
  </label>
</#macro>

<#macro formField
  id=""
  offset=false
>
  <div
    <#if id?has_content>id="${id}"</#if>
    class="<#if globalFormType == "horizontal">col-md-${globalFieldWidth} <#if offset>col-md-offset-${globalLabelWidth}</#if></#if>"
    <#if globalFormType == "inline">style="display: inline-block"</#if>
  >
    <#nested>
  </div>
</#macro>

<#macro input
  name label
  type="text"
  required=false
  disabled=false
  autofocus=false
  pattern="" title=""
  help=""
  addon=""
>
  <@formGroup>
    <@formLabel for=name value=label/>
    <@formField>
      <#if addon?has_content>
        <div class="input-group">
      </#if>
          <input
            type="${type}" id="${name}" name="${name}" class="${fieldInputClass()}"
            <#if globalFormValues[name]??>value="${globalFormValues[name]}"</#if>
            <#if required>required</#if>
            <#if disabled>disabled</#if>
            <#if autofocus>autofocus</#if>
            <#if pattern?has_content>pattern="${pattern}"</#if>
            <#if title?has_content>title="${title}"</#if>
          >
          <#if help?has_content>
            <span class="help-block">${help}</span>
          </#if>
      <#if addon?has_content>
          <div class="input-group-addon">${addon}</div>
        </div>
      </#if>
    </@formField>
  </@formGroup>
</#macro>

<#macro password
  name label
  required=false
>
  <@formGroup>
    <@formLabel for=name value=label/>
    <@formField>
      <input type="password" id="${name}" name="${name}" class="${fieldInputClass()}" <#if required>required</#if>>
    </@formField>
  </@formGroup>
</#macro>

<#macro textarea
  name label
  class=""
>
  <@formGroup>
    <@formLabel for=name value=label/>
    <@formField>
      <#nested>
      <textarea rows="5" id="${name}" name="${name}" class="${class} ${fieldInputClass()}">${globalFormValues[name]!""}</textarea>
    </@formField>
  </@formGroup>
</#macro>

<#macro select
  name label options
  disabled=false
  help=""
>
  <@formGroup>
    <@formLabel for=name value=label/>
    <@formField>
      <select id="${name}" name="${name}" class="${fieldInputClass()}" <#if disabled>disabled</#if>>
        <#list options as k, v>
          <option value="${k}" ${(k == globalFormValues[name]!"")?then("selected", "")}>${v}</option>
        </#list>
      </select>
      <#if help?has_content>
        <span class="help-block">${help}</span>
      </#if>
    </@formField>
  </@formGroup>
</#macro>

<#macro file
  name label
  required=false
>
  <@formGroup>
    <@formLabel for=name value=label/>
    <@formField>
      <input type="file" id="${name}" name="${name}" class="${fieldInputClass()}">
    </@formField>
  </@formGroup>
</#macro>

<#macro csv name>
  <@formGroup>
    <textarea rows="10" id="${name}" name="${name}" class="${fieldInputClass()}" autofocus>${globalFormValues[name]!""}</textarea>
  </@formGroup>
</#macro>

<#macro submit
  size=""
>
  <#if globalFormType == "inline">
    <@ui.button type="submit" size="xs">
      <#nested>
    </@ui.button>
  <#else>
    <@formGroup>
      <@formField offset=true>
        <@ui.button type="submit" size=size>
          <#nested>
        </@ui.button>
      </@formField>
    </@formGroup>
  </#if>
</#macro>
