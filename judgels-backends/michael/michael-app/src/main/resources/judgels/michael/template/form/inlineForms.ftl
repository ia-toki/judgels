<#macro form>
  <form method="POST" class="form-inline">
    <#nested>
  </form>
</#macro>

<#macro select name label options form={}>
  <div class="form-group">
    <label for="${name}"><span class="small">${label}</span></label>
    <select id="${name}" name="${name}">
      <#list options as k, v>
        <option value="${k}" ${(k == form[name]!"")?then("selected", "")}>${v}</option>
      </#list>
    </select>
  </div>
</#macro>

<#macro submit>
  <button type="submit" class="btn btn-primary btn-xs">
    <#nested>
  </button>
</#macro>
