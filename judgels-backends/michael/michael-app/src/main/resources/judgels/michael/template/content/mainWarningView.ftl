<#macro view html>
  <#if !html?has_content>
    <#return>
  </#if>
  <div class="alert alert-danger">
    ${html?no_esc}
  </div>
</#macro>
