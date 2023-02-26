<#-- @ftlvariable type="judgels.michael.template.problem.base.ListProblemsView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/template/table/tableLayout.ftl" as table>
<#import "/judgels/michael/template/table/paginationView.ftl" as pagination>

<@template.layout>
  <@table.layout>
    <thead>
      <tr>
        <th class="table-col-id">ID</th>
        <th>Slug</th>
        <th>Created by</th>
        <th>Last updated at</th>
      </tr>
    </thead>
    <tbody>
      <#list problems.page as problem>
        <tr>
          <td>${problem.id}</td>
          <td>${problem.slug}</td>
          <td>${profilesMap[problem.authorJid].username}</td>
          <td>${getDurationFromNow(problem.lastUpdateTime)}</td>
        </tr>
      </#list>
    </tbody>
  </@table.layout>
  <@pagination.view page=problems filterString=filterString/>
</@template.layout>
