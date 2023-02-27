<#macro view tabs activeTab>
  <#if tabs?size == 0>
    <#return>
  </#if>

  <nav class="navbar navbar-default">
    <div class="container-fluid">
      <ul class="nav navbar-nav">
        <#list tabs as tab>
          <li class="${(tab.key == activeTab)?then("active", "")}">
            <a href="${tab.target}">${tab.label}</a>
          </li>
        </#list>
      </ul>
    </div>
  </nav>
</#macro>
