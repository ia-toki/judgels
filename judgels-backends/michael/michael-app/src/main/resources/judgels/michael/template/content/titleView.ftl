<#macro view title buttons>
  <#if buttons?size == 0>
    <h2>${title}</h2>
   <#else>
    <div class="title-inline">
      <h2>${title}</h2>
      <#list buttons as button>
        <a class="btn btn-primary btn-sm" href="${button.target}">${button.label}</a>
      </#list>
    </div>
   </#if>
</#macro>
