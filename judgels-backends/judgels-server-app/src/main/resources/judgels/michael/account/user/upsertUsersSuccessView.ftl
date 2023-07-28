<#-- @ftlvariable type="judgels.michael.account.user.UpsertUsersSuccessView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/ui.ftl" as ui>
<#import "/judgels/michael/forms.ftl" as forms>

<@template.layout>
  <div class="alert alert-success" role="alert">
    ${createdUsernames?size} users created, ${updatedUsernames?size} users updated.
  </div>

  <@ui.buttonLink intent="default" to="/accounts/users">Back</@ui.buttonLink>

  <hr>

  <#if (createdUsernames?size > 0)>
    <h4>Created users</h4>
    <@ui.table>
      <thead>
        <tr>
          <th>Username</th>
        </tr>
      </thead>
      <tbody>
        <#list createdUsernames as username>
          <tr>
            <td>${username}</td>
            </td>
          </tr>
        </#list>
      </tbody>
    </@ui.table>
  </#if>

  <#if (updatedUsernames?size > 0)>
    <h4>Updated users</h4>
    <@ui.table>
      <thead>
        <tr>
          <th>Username</th>
        </tr>
      </thead>
      <tbody>
        <#list updatedUsernames as username>
          <tr>
            <td>${username}</td>
            </td>
          </tr>
        </#list>
      </tbody>
    </@ui.table>
  </#if>
</@template.layout>
