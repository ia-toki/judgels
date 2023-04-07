<#-- @ftlvariable type="judgels.michael.problem.bundle.submission.ViewSubmissionsView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>

<@template.layout>
  <h3>Submission #${submission.id}</h3>

  <h4>General</h4>
  <div class="panel panel-default">
    <div class="panel-body">
      <table class="table table-condensed">
        <thead>
          <tr>
            <th class="col-md-3">Info</th>
            <th class="col-md-9">Value</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>Author</td><td>${profile.username}</td>
          </tr>
          <tr>
            <td>Score</td><td>${submission.latestGrading.score}</td>
          </tr>
          <tr>
            <td>Submitted at</td><td>${getDateFromInstant(submission.time)?datetime?string.long}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>

  <h4>Results</h4>
  <div class="panel panel-default">
    <div class="panel-body">
      <table class="table table-condensed">
        <thead>
          <tr>
            <th class="col-md-4">No.</th>
            <th class="col-md-4">Answer</th>
            <th class="col-md-2">Score</th>
            <th class="col-md-2"></th>
          </tr>
        </thead>

        <tbody>
          <#list gradingResults as jid, result>
            <tr>
                <td>${result.number}</td>
                <td>${answer.answers[jid]}</td>
                <td>${result.score}</td>
                <td></td>
            </tr>
          </#list>
        </tbody>
      </table>
    </div>
  </div>
</@template.layout>
