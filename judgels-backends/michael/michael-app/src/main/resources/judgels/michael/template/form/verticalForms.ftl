<#macro form>
  <form method="POST" class="form-vertical">
    <#nested>
  </form>
</#macro>

<#macro text name label form={} required=false>
  <div class="form-group">
    <label class="control-label" for="${name}">${label}</label>
    <input
      type="text" id="${name}" name="${name}" class="form-control"
      <#if form[name]??>value="${form[name]}"</#if>
      <#if required>required</#if>
    >
  </div>
</#macro>

<#macro submit>
  <div class="form-group">
    <button type="submit" class="btn btn-primary">
      <#nested>
    </button>
  </div>
</#macro>
