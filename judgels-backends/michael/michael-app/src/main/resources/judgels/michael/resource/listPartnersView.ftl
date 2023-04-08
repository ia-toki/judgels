<#-- @ftlvariable type="judgels.michael.resource.ListPartnersView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/template/ui/tables.ftl" as tables>

<@template.layout>
  <h3>Partners</h3>
  <#if partners?size == 0>
    <p>No partners.</p>
  <#else>
    <@tables.table>
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
    </@tables.table>
  </#if>
</@template.layout>
