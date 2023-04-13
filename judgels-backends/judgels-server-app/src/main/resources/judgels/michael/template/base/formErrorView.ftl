<#macro view message>
  <#if message?has_content>
    <br>
    <div class="alert alert-danger" role="alert">
      ${message}
    </div>
  </#if>
</#macro>
