<#-- @ftlvariable type="judgels.michael.problem.programming.submission.ViewSubmissionView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/template/table/tableLayout.ftl" as table>

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
            <td>Language</td><td>${gradingLanguageName}</td>
          </tr>
          <tr>
            <td>Verdict</td><td>${submission.latestGrading.get().verdict.getName()}</td>
          </tr>
          <tr>
            <td>Score</td><td>${submission.latestGrading.get().score}</td>
          </tr>
          <tr>
            <td>Submitted at</td><td>${getDateFromInstant(submission.time)?datetime?string.long}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>

  <#if details.isPresent() && details.get().errorMessage.isPresent()>
    <h4>Error message</h4>
    <pre>${details.get().errorMessage.get()}</pre>
  </#if>

  <#if (details.isPresent() && details.get().subtaskResults?size >= 1 && details.get().subtaskResults[0].id != -1)>
    <h4>Subtask results</h4>
    <div class="panel panel-default">
      <div class="panel-body">
        <table class="table table-condensed">
          <thead>
            <tr>
              <th class="col-md-1">ID</th>
              <th class="col-md-3">Verdict</th>
              <th class="col-md-2">Score</th>
              <th class="col-md-6"></th>
            </tr>
          </thead>

          <tbody>
            <#list details.get().subtaskResults as subtaskResult>
              <tr>
                <td>${subtaskResult?index + 1}</td>
                <td>${subtaskResult.verdict.getName()}</td>
                <td>${subtaskResult.score}</td>
                <td></td>
              </tr>
            </#list>
          </tbody>
        </table>
      </div>
    </div>
  </#if>

  <h4>Sample test data results</h4>

  <#if (!details.isPresent() || details.get().testDataResults?size < 1)>
    <p>No info.</p>
  <#else>
    <div class="panel panel-default">
      <div class="panel-body">
        <table class="table table-condensed">
          <thead>
            <tr>
              <th class="col-md-1">Id</th>
              <th class="col-md-3">Verdict</th>
              <th class="col-md-2">Score</th>
              <th class="col-md-2">Time</th>
              <th class="col-md-2">Memory</th>
              <#if (details.get().subtaskResults?size >= 0 && details.get().subtaskResults[0].id != -1)>
                <th class="col-md-2">Subtasks</th>
              </#if>
            </tr>
          </thead>

          <tbody>
            <#list details.get().testDataResults[0].testCaseResults as testCaseResult>
              <tr>
                <td>${testCaseResult?index + 1}</td>
                <td>${testCaseResult.verdict.getName()}</td>
                <td>${testCaseResult.score}</td>
                <td>${testCaseResult.executionResult.isPresent()?then(testCaseResult.executionResult.get().time?string + " ms", "?")}</td>
                <td>${testCaseResult.executionResult.isPresent()?then(testCaseResult.executionResult.get().memory?string + " KB", "?")}</td>
                <#if (details.get().subtaskResults?size >= 0 && details.get().subtaskResults[0].id != -1)>
                  <td>
                    <#list testCaseResult.subtaskIds as id>
                      <#if (id > 0)><span class="badge" style="margin-right: 1px">${id}</span></#if>
                    </#list>
                  </td>
                </#if>
              </tr>
            </#list>
          </tbody>
        </table>
      </div>
    </div>
  </#if>

  <h4>Test data results</h4>

  <#if (!details.isPresent() || details.get().testDataResults?size < 2)>
    <p>No info.</p>
  <#else>
    <#list 1..<details.get().testDataResults?size as i>
      <div class="panel panel-default">
        <#if details.get().testDataResults[i].id != -1>
          <div class="panel-heading">
            Test group ${details.get().testDataResults[i].id}
          </div>
        </#if>
        <div class="panel-body">
          <table class="table table-condensed">
            <thead>
              <tr>
                <th class="col-md-1">Id</th>
                <th class="col-md-3">Verdict</th>
                <th class="col-md-2">Score</th>
                <th class="col-md-2">Time</th>
                <th class="col-md-2">Memory</th>
                <#if (details.get().subtaskResults?size >= 0 && details.get().subtaskResults[0].id != -1)>
                  <th class="col-md-2">Subtasks</th>
                </#if>
              </tr>
            </thead>
            <tbody>
              <#list details.get().testDataResults[i].testCaseResults as testCaseResult>
                <tr>
                  <td>${testCaseResult?index + 1}</td>
                  <td>${testCaseResult.verdict.getName()}</td>
                  <td>${testCaseResult.score}</td>
                  <td>${testCaseResult.executionResult.isPresent()?then(testCaseResult.executionResult.get().time?string + " ms", "?")}</td>
                  <td>${testCaseResult.executionResult.isPresent()?then(testCaseResult.executionResult.get().memory?string + " KB", "?")}</td>
                  <#if (details.get().subtaskResults?size >= 0 && details.get().subtaskResults[0].id != -1)>
                    <td>
                      <#list testCaseResult.subtaskIds as id>
                        <span class="badge" style="margin-right: 1px">${id}</span>
                      </#list>
                    </td>
                  </#if>
                </tr>
              </#list>
            </tbody>
          </table>
        </div>
      </div>
    </#list>
  </#if>

  <#if !isOutputOnly>
    <h4>Source files</h4>
    <#list sourceFiles as key, sourceFile>
      <div class="panel panel-default">
        <div class="panel-heading">${key}: ${sourceFile.name}</div>
          <div class="panel-body">
            <pre>${getSourceFileContent(sourceFile)}</pre>
            <#if details.isPresent() && details.get().compilationOutputs[key]??>
              <p><strong>Compilation output</strong></p>
              <pre>${details.get().compilationOutputs[key]}</pre>
            </#if>
          </div>
        </div>
      </div>
    </#list>

    <#if details.isPresent()>
      <#list details.get().compilationOutputs as key, compilationOutput>
        <#if !sourceFiles[key]??>
          <div class="panel panel-default">
            <div class="panel-body">
              <p><strong>Compilation output</strong></p>
              <pre>${compilationOutput}</pre>
            </div>
          </div>
        </#if>
      </#list>
    </#if>
  </#if>
</@template.layout>
