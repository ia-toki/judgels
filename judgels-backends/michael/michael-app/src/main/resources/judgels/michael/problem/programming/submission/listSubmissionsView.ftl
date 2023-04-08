<#-- @ftlvariable type="judgels.michael.problem.programming.submission.ListSubmissionsView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/template/ui/buttons.ftl" as buttons>
<#import "/judgels/michael/template/ui/tables.ftl" as tables>

<@template.layout>
  <#if canEdit>
    <@buttons.link intent="default" size="sm" to="submissions/regrade" disabled=!canSubmit style="margin-bottom: 10px" onclick="return confirm('Will regrade ALL submissions in ALL pages, are you sure?')">
      <span class="glyphicon glyphicon-refresh"></span> Regrade all
    </@buttons.link>
  </#if>

  <@tables.table>
    <thead>
      <tr>
        <th style="min-width: 50px">ID</th>
        <th>Author</th>
        <th>Language</th>
        <th>Verdict</th>
        <th>Score</th>
        <th>Submitted at</th>
        <th class="col-fit"></th>
      </tr>
    </thead>
    <tbody>
      <#list submissions.page as submission>
        <tr>
          <td class="col-fit">${submission.id}</td>
          <td>${profilesMap[submission.userJid].username}</td>
          <td>${gradingLanguageNamesMap[submission.gradingLanguage]}</td>
          <td>${submission.latestGrading.get().verdict.getName()}</td>
          <td>${submission.latestGrading.get().score}</td>
          <td>${getFormattedDurationFromNow(submission.time)}</td>
          <td class="col-fit">
            <@buttons.link size="xs" to="submissions/${submission.id}">View</@buttons.link>
            <#if canEdit>
              <@buttons.link intent="default" size="xs" to="submissions/${submission.id}/regrade" disabled=!canSubmit>
                <span class="glyphicon glyphicon-refresh"></span>
              </@buttons.link>
            </#if>
          </td>
        </tr>
      </#list>
    </tbody>
  </@tables.table>
  <@tables.pagination page=submissions/>
</@template.layout>
