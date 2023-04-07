<#macro form action="">
  <form method="POST" class="form-horizontal" <#if action?has_content>action="${action}"</#if>>
    <#nested>
  </form>
</#macro>

<#macro multipartForm action="">
  <form method="POST" class="form-horizontal" enctype="multipart/form-data" <#if action?has_content>action="${action}"</#if>>
    <#nested>
  </form>
</#macro>

<#function leftCol compact>
  <#return compact?then("col-md-2", "col-md-3")>
</#function>

<#function rightCol compact>
  <#return compact?then("col-md-10", "col-md-9")>
</#function>

<#function rightColWithOffset compact>
  <#return compact?then("col-md-10 col-md-offset-2", "col-md-9 col-md-offset-3")>
</#function>

<#macro text
  name label form={}
  compact=false
  number=false
  required=false
  disabled=false
  autofocus=false
  pattern="" title=""
  help=""
  addon=""
>
  <div class="form-group">
    <label class="control-label ${leftCol(compact)}" for="${name}">${label}</label>
    <div class="${rightCol(compact)}">
      <#if addon?has_content>
        <div class="input-group">
      </#if>
          <input
            type="${number?then("number", "text")}" id="${name}" name="${name}" class="form-control"
            <#if form[name]??>value="${form[name]}"</#if>
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
    </div>
  </div>
</#macro>

<#macro password name label required=false>
  <div class="form-group">
    <label class="control-label col-md-3" for="${name}">${label}</label>
    <div class="col-md-9">
      <input type="password" id="${name}" name="${name}" class="form-control" ${required?then("required", "")}>
    </div>
  </div>
</#macro>

<#macro select name label options form={} disabled=false help="" simple=false>
  <div class="form-group">
    <label class="<#if !simple>control-label </#if> col-md-3" for="${name}">${label}</label>
    <div class="col-md-9">
      <select id="${name}" name="${name}" class="<#if !simple>form-control</#if>" <#if disabled>disabled</#if>/>
        <#list options as k, v>
          <option value="${k}" ${(k == form[name]!"")?then("selected", "")}>${v}</option>
        </#list>
      </select>
      <#if help?has_content>
        <span class="help-block">${help}</span>
      </#if>
    </div>
  </div>
</#macro>

<#macro textarea name label form={} compact=false class="">
  <div class="form-group">
    <label class="control-label ${leftCol(compact)}" for="${name}">${label}</label>
    <div class="${rightCol(compact)}">
      <#nested>
      <textarea rows="5" id="${name}" name="${name}" class="form-control ${class}">${form[name]!""}</textarea>
    </div>
  </div>
</#macro>

<#macro file name label form={} simple=false required=false>
  <div class="form-group">
    <label class="<#if !simple>control-label </#if>col-md-3" for="${name}">${label}</label>
    <div class="col-md-9">
      <input type="file" id="${name}" name="${name}" class="<#if !simple>form-control</#if>">
    </div>
  </div>
</#macro>

<#macro submit compact=false small=false>
  <div class="form-group">
    <div class="${rightColWithOffset(compact)}">
      <button type="submit" class="btn btn-primary <#if small>btn-sm</#if>">
        <#nested>
      </button>
    </div>
  </div>
</#macro>
