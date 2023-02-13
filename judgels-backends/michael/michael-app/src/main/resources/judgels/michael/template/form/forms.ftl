<#macro form>
  <form method="POST" class="form-horizontal">
    <#nested>
  </form>
</#macro>

<#macro text name label required=false>
  <div class="form-group">
    <label class="control-label col-md-3" for="${name}">${label}</label>
    <div class="col-md-9">
      <input type="text" id="${name}" name="${name}" class="form-control" ${required?then("required", "")}>
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

<#macro submit>
  <div class="form-group">
    <div class="col-md-9 col-md-offset-3">
      <button type="submit" class="btn btn-primary">
        <#nested>
      </button>
    </div>
  </div>
</#macro>
