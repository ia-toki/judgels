<#macro view menus activeMenu>
  <div class="sidebar clearfix">
    <nav class="menus">
      <ul class="nav nav-stacked">
        <#list menus as menu>
          <li class="${(menu.key == activeMenu)?then("active", "")}">
            <a href="${menu.target}">${menu.label}</a>
            <div class="menu-arrow">&gt;</div>
          </li>
        </#list>
      </ul>
    </nav>
  </div>
</#macro>
