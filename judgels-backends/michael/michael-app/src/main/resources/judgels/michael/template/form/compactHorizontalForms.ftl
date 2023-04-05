<#macro form>
  <form method="POST" class="form-horizontal">
    <#nested>
  </form>
</#macro>

<#macro text name label form={} required=false disabled=false autofocus=false pattern="" title="" help="" addon="">
  <div class="form-group">
    <label class="control-label col-md-2" for="${name}">${label}</label>
    <div class="col-md-10">
      <#if addon?has_content>
        <div class="input-group">
      </#if>
          <input
            type="text" id="${name}" name="${name}" class="form-control"
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

<#macro textarea name label form={} class="">
  <div class="form-group">
    <label class="control-label col-md-2" for="${name}">${label}</label>
    <div class="col-md-10">
      <#nested>
      <textarea rows="5" id="${name}" name="${name}" class="form-control ${class}">${form[name]!""}</textarea>
    </div>
  </div>
</#macro>

<#macro submit>
  <div class="form-group">
    <div class="col-md-10 col-md-offset-2">
      <button type="submit" class="btn btn-primary">
        <#nested>
      </button>
    </div>
  </div>
</#macro>
