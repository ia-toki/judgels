<#macro form>
  <form method="POST" class="form-horizontal">
    <#nested>
  </form>
</#macro>

<#macro text name label required=false value="" pattern="" title="">
  <div class="form-group">
    <label class="control-label col-md-3" for="${name}">${label}</label>
    <div class="col-md-9">
      <input
        type="text" id="${name}" name="${name}" class="form-control" value="${value}"
        <#if required>required</#if>
        <#if pattern?has_content>pattern="${pattern}"</#if>
        <#if title?has_content>title="${title}"</#if>
      >
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

<#macro select name label options value>
  <div class="form-group">
    <label class="control-label col-md-3" for="${name}">${label}</label>
    <div class="col-md-9">
      <select id="${name}" name="${name}" class="form-control">
        <#list options as k, v>
          <option value="${k}" ${(k == value)?then("selected", "")}>${v}</option>
        </#list>
      </select>
    </div>
  </div>
</#macro>

<#macro textarea name label value="">
  <div class="form-group">
    <label class="control-label col-md-3" for="${name}">${label}</label>
    <div class="col-md-9">
      <textarea id="${name}" name="${name}" class="form-control">${value}</textarea>
    </div>
  </div>
</#macro>

<#macro submit>
  <div class="form-group">
    <div class="col-md-9 col-md-offset-3">
      <button type="submit" class="btn btn-primary">
        <#nested>
      </button>
    </div>
  </div>
</#macro>
