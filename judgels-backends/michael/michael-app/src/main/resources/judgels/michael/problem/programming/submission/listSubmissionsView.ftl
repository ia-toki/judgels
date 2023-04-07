<#-- @ftlvariable type="judgels.michael.problem.programming.submission.ListSubmissionsView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/template/table/tableLayout.ftl" as table>
<#import "/judgels/michael/template/table/paginationView.ftl" as pagination>

<@template.layout>
  <#if canEdit>
    <a type="button" class="btn btn-primary btn-sm" style="margin-bottom: 10px" href="submissions/regrade" <#if !canSubmit>disabled</#if> onclick="return confirm('Will regrade ALL submissions in ALL pages, are you sure?')">
      <span class="glyphicon glyphicon-refresh"></span> Regrade all
    </a>
  </#if>

  <@table.layout>
    <thead>
      <tr>
        <th class="table-col-id">ID</th>
        <th>Author</th>
        <th>Language</th>
        <th>Verdict</th>
        <th>Score</th>
        <th>Time</th>
        <th class="table-col-submission-actions"></th>
      </tr>
    </thead>
    <tbody>
      <#list submissions.page as submission>
        <tr>
          <td>${submission.id}</td>
          <td>${profilesMap[submission.userJid].username}</td>
          <td>${gradingLanguageNamesMap[submission.gradingLanguage]}</td>
          <td>${submission.latestGrading.get().verdict.getName()}</td>
          <td>${submission.latestGrading.get().score}</td>
          <td>${getFormattedDurationFromNow(submission.time)}</td>
          <td class="text-center">
            <a type="button" class="btn btn-primary btn-xs" href="submissions/${submission.id}">View</a>
            <#if canEdit>
              <a type="button" class="btn btn-default btn-xs" href="submissions/${submission.id}/regrade" <#if !canSubmit>disabled</#if>>
                <span class="glyphicon glyphicon-refresh"></span>
              </a>
            </#if>
          </td>
        </tr>
      </#list>
    </tbody>
  </@table.layout>
  <@pagination.view page=submissions/>
</@template.layout>
