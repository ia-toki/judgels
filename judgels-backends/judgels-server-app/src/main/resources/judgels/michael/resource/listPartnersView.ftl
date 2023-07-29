<#-- @ftlvariable type="judgels.michael.resource.ListPartnersView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/ui.ftl" as ui>

<@template.layout>
  <#if partners?size == 0>
    <p>No partners.</p>
  <#else>
    <@ui.table>
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
    </@ui.table>
  </#if>
</@template.layout>
