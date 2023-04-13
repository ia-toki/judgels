<#-- @ftlvariable type="judgels.michael.problem.bundle.submission.ListSubmissionsView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/ui.ftl" as ui>

<@template.layout>
  <#if canEdit>
    <@ui.buttonLink size="xs" to="submissions/regrade" disabled=!canSubmit style="margin-bottom: 10px" onclick="return confirm('Will regrade ALL submissions in ALL pages, are you sure?')">
      <span class="glyphicon glyphicon-refresh"></span> Regrade all
    </@ui.buttonLink>
  </#if>

  <@ui.table>
    <thead>
      <tr>
        <th style="min-width: 50px">ID</th>
        <th>Author</th>
        <th>Score</th>
        <th>Submitted at</th>
        <th class="col-fit"></th>
      </tr>
    </thead>
    <tbody>
      <#list submissions.page as submission>
        <tr>
          <td class="col-fit">${submission.id}</td>
          <td>${profilesMap[submission.authorJid].username}</td>
          <td>${submission.latestGrading.score}</td>
          <td>${getFormattedDurationFromNow(submission.time)}</td>
          <td class="col-fit">
            <@ui.buttonLink size="xs" to="submissions/${submission.id}">View</@ui.buttonLink>
            <#if canEdit>
              <@ui.buttonLink intent="default" size="xs" to="submissions/${submission.id}/regrade" disabled=!canSubmit>
                <span class="glyphicon glyphicon-refresh"></span>
              </@ui.buttonLink>
            </#if>
          </td>
        </tr>
      </#list>
    </tbody>
  </@ui.table>
  <@ui.pagination page=submissions/>
</@template.layout>
