<#-- @ftlvariable type="judgels.michael.account.user.ListUsersView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/ui.ftl" as ui>

<@template.layout>
  <h3>Users</h3>
  <@ui.table>
    <thead>
      <tr>
        <th>Username</th>
        <th>Last login at</th>
      </tr>
    </thead>
    <tbody>
      <#list users.page as user>
        <tr>
          <td>${user.username}</td>
          <td><#if lastSessionTimesMap[user.jid]??>${getFormattedDurationFromNow(lastSessionTimesMap[user.jid])}</#if></td>
          </td>
        </tr>
      </#list>
    </tbody>
  </@ui.table>
  <@ui.pagination page=users/>
</@template.layout>
