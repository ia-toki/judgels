<#-- @ftlvariable type="judgels.michael.problem.ListProblemsView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/template/ui/buttons.ftl" as buttons>
<#import "/judgels/michael/template/ui/tables.ftl" as tables>

<@template.layout>
  <@tables.table>
    <thead>
      <tr>
        <th style="min-width: 50px">ID</th>
        <th>Slug</th>
        <th>Created by</th>
        <th>Last updated at</th>
        <th class="col-fit"></th>
      </tr>
    </thead>
    <tbody>
      <#list problems.page as problem>
        <tr>
          <td class="col-fit">${problem.id}</td>
          <td>${problem.slug}</td>
          <td>${profilesMap[problem.authorJid].username}</td>
          <td>${getFormattedDurationFromNow(problem.lastUpdateTime)}</td>
          <td class="col-fit">
            <@buttons.link size="xs" to="/problems/${problem.type?lower_case}/${problem.id}/statements">Manage</@buttons.link>
          </td>
        </tr>
      </#list>
    </tbody>
  </@tables.table>
  <@tables.pagination page=problems filterString=filterString tags=tags/>
</@template.layout>
