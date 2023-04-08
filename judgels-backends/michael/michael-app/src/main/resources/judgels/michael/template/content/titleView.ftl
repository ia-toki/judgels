<#import "/judgels/michael/template/ui/buttons.ftl" as btns>

<#macro view title buttons>
  <#if buttons?size == 0>
    <h2>${title}</h2>
   <#else>
    <div class="title-inline">
      <h2>${title}</h2>
      <#list buttons as button>
        <@btns.link size="sm" to="${button.target}">${button.label}</@btns.link>
      </#list>
    </div>
   </#if>
</#macro>
