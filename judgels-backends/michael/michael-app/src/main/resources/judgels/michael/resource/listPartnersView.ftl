<#-- @ftlvariable type="judgels.michael.resource.ListPartnersView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/template/table/tableLayout.ftl" as table>

<@template.layout>
  <h3>Partners</h3>
  <@table.layout>
    <thead>
      <tr>
        <th>User</th>
        <th class="table-col-permissions">Permission</th>
      </tr>
    </thead>
    <tbody>
      <#list partners as partner>
        <tr>
          <td>${profilesMap[partner.userJid].username}</td>
          <td>${partner.permission}</td>
        </tr>
      </#list>
    </tbody>
  </@table.layout>
</@template.layout>
