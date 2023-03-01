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
        <th class="table-col-actions"></th>
      </tr>
    </thead>
    <tbody>
      <#list problems.page as problem>
        <tr>
          <td>${problem.id}</td>
          <td>${problem.slug}</td>
          <td>${profilesMap[problem.authorJid].username}</td>
          <td>${getDurationFromNow(problem.lastUpdateTime)}</td>
          <td class="text-center">
            <a type="button" class="btn btn-primary btn-xs" href="/problems/${problem.type?lower_case}/${problem.id}/statements">Manage</a>
          </td>
        </tr>
      </#list>
    </tbody>
  </@table.layout>
  <@pagination.view page=problems filterString=filterString tags=tags/>
</@template.layout>
