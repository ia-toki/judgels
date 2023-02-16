<#macro view name username avatarUrl>
  <header>
    <div class="container">
      <div class="row">
        <div class="col-md-12">
            <div class="pull-left">
              <a href="/"><img class="logo" src="/assets/images/logo.png" alt="Logo"/></a>
            </div>
            <div class="title pull-left">
              <a href="/"><h1>${name}</h1></a>
            </div>
            <div class="subtitle pull-left">
              | Administration
            </div>
            <#if username?has_content>
              <div class="pull-right">
                <div class="avatar-username">${username}</div>
                <a class="avatar-logout" href="/logout">Log out</a>
              </div>
              <div class="avatar-image pull-right">
                <img src="${avatarUrl}"/>
              </div>
            </#if>
        </div>
      </div>
    </div>
  </header>
</#macro>
