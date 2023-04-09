<#-- @ftlvariable type="judgels.michael.problem.ListProblemsView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/ui.ftl" as ui>

<@template.layout>
  <@ui.table>
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
            <@ui.buttonLink size="xs" to="/problems/${problem.type?lower_case}/${problem.id}/statements">Manage</@ui.buttonLink>
          </td>
        </tr>
      </#list>
    </tbody>
  </@ui.table>
  <@ui.pagination page=problems filterString=filterString tags=tags/>
</@template.layout>
