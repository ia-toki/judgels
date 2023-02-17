<#macro view menus>
  <div class="sidebar clearfix">
    <nav class="menus">
      <ul class="nav nav-stacked">
        <#list menus as menu>
          <li role="presentation"><a href="${menu.target}">${menu.label}</a><div class="menu-arrow">&gt;</div></li>
        </#list>
      </ul>
    </nav>
  </div>
</#macro>
