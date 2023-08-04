<#-- @ftlvariable type="judgels.michael.account.role.ListRolesView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/ui.ftl" as ui>

<@template.layout>
  <p>List of users with the corresponding role for account, problem/lesson, contest, and training management. <a href="https://judgels.toki.id/docs/account-management/roles" target="_blank">Learn more <span class="glyphicon glyphicon-new-window"></span></a></p>

  <hr>

  <@ui.table>
    <thead>
      <tr>
        <th>User</th>
        <th style="width: 120px">Account</th>
        <th style="width: 120px">Problem/Lesson</th>
        <th style="width: 120px">Contest</th>
        <th style="width: 120px">Training</th>
      </tr>
    </thead>
    <tbody>
      <#list userWithRoles as userWithRole>
        <tr>
          <td><#if profilesMap[userWithRole.userJid]??>${profilesMap[userWithRole.userJid].username}<#else>${userWithRole.userJid}</#if></td>
          <td>${userWithRole.role.jophiel.isPresent()?then(userWithRole.role.jophiel.get(), "")}</td>
          <td>${userWithRole.role.sandalphon.isPresent()?then(userWithRole.role.sandalphon.get(), "")}</td>
          <td>${userWithRole.role.uriel.isPresent()?then(userWithRole.role.uriel.get(), "")}</td>
          <td>${userWithRole.role.jerahmeel.isPresent()?then(userWithRole.role.jerahmeel.get(), "")}</td>
          </td>
        </tr>
      </#list>
    </tbody>
  </@ui.table>
</@template.layout>
