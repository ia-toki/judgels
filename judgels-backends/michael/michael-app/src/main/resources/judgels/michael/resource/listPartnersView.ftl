<#-- @ftlvariable type="judgels.michael.resource.ListPartnersView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/template/table/tableLayout.ftl" as table>

<@template.layout>
  <h3>Partners</h3>
  <#if partners?size == 0>
    <p>No partners.</p>
  <#else>
    <@table.layout>
      <thead>
        <tr>
          <th>User</th>
          <th class="col-fit">Permission</th>
        </tr>
      </thead>
      <tbody>
        <#list partners as partner>
          <tr>
            <td>${profilesMap[partner.userJid].username}</td>
            <td class="col-fit">${partner.permission}</td>
          </tr>
        </#list>
      </tbody>
    </@table.layout>
  </#if>
</@template.layout>
