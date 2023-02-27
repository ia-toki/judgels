<#macro view tabs activeTab>
  <#if tabs?size == 0>
    <#return>
  </#if>

  <ul class="nav nav-tabs">
    <#list tabs as tab>
      <li class="${(tab.key == activeTab)?then("active", "")}">
        <a href="${tab.target}">${tab.label}</a>
      </li>
    </#list>
  </ul>
  <br>
</#macro>
