<#import "/judgels/michael/ui.ftl" as ui>

<#macro view title buttons>
  <#if buttons?size == 0>
    <h2>${title}</h2>
   <#else>
    <div class="title-inline">
      <h2>${title}</h2>
      <#list buttons as button>
        <@ui.buttonLink size="sm" to="${button.target}">${button.label}</@ui.buttonLink>
      </#list>
    </div>
   </#if>
</#macro>
