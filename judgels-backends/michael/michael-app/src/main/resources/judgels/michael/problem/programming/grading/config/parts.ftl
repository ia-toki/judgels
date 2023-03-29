<#import "/judgels/michael/template/form/horizontalForms.ftl" as forms>

<#macro limits>
  <@forms.text form=form name="timeLimit" label="Time limit" addon="milliseconds" required=true disabled=!canEdit/>
  <@forms.text form=form name="memoryLimit" label="Memory limit" addon="kilobytes" required=true disabled=!canEdit/>
</#macro>

<#macro keys>
  <@forms.text form=form name="sourceFileFieldKeys" label="Keys" addon="comma-separated keys" required=true disabled=!canEdit/>
</#macro>

<#macro testCase isTemplate=false inputFile="" outputFile="" hasOutput=true>
  <tr class="test-case <#if isTemplate>test-case-template</#if>">
    <td>
      <input class="form-control input-sm test-case-input" type="text" disabled value="${inputFile}">
    </td>
    <td <#if !hasOutput>style="display: none"</#if>>
      <input class="form-control input-sm test-case-output" type="text" disabled value="${outputFile}">
    </td>
    <#if canEdit>
      <td class="text-center">
        <a href="#" class="test-case-remove-button">
          <span class="glyphicon glyphicon-remove"></span>
        </a>
      </td>
    </#if>
  </tr>
</#macro>

<#macro addTestCaseForm hasOutput=true>
  <#if !canEdit><#return></#if>

  <tr class="active test-case-add-form">
    <td>
      <select class="test-case-input">
        <#list testDataFiles as file>
          <option value="${file.name}">${file.name}</option>
        </#list>
      </select>
    </td>
    <#if hasOutput>
      <td>
        <select class="test-case-output">
          <#list testDataFiles as file>
            <option value="${file.name}">${file.name}</option>
          </#list>
        </select>
      </td>
    <#else>
      <td style="display: none">
        <select class="test-case-output" value=""></select>
      </td>
    </#if>
    <td class="text-center">
      <a href="#" class="test-case-add-button"><span class="glyphicon glyphicon-plus"></span></a>
    </td>
  </tr>
</#macro>

<#macro subtaskCheckbox value checked=false>
  <label class="checkbox-inline">
    <input type="checkbox" value="${value}" <#if checked>checked</#if> <#if !canEdit>disabled</#if>> <span>${value}</span>
  </label>
</#macro>

<#macro subtaskAssignment name="" subtaskIds=[]>
  Assign to subtasks:

  <div>
    <#list 1..defaultSubtaskCount as i>
      <@subtaskCheckbox value=i checked=subtaskIds?seq_contains(i?string)/>
    </#list>
  </div>
</#macro>

<#macro sampleTestCaseSubtaskAssignment isTemplate=false subtaskIds=[]>
  <tr class="subtask-assignment <#if isTemplate>subtask-assignment-template</#if>">
    <td colspan="3">
      <@subtaskAssignment subtaskIds=subtaskIds/>
      <#nested>
      <br>
    </td>
  </tr>
</#macro>

<#macro testGroupSubtaskAssignment subtaskIds=[]>
  <div class="subtask-assignment">
    <hr>
    <@subtaskAssignment subtaskIds=subtaskIds/>
    <#nested>
  </div>
</#macro>

<#function splitCsv csv>
  <#return (csv!"")?has_content?then((csv!"")?split(","), [])>
</#function>

<#macro testGroup isTemplate=false heading="" inputFiles=[] outputFiles=[] hasOutput=true>
  <div class="panel panel-default test-group <#if isTemplate>test-group-template</#if>">
    <#if heading?has_content>
      <div class="panel-heading">
        <span>${heading}</span>
        <a href="#" class="test-group-remove-button" <#if !canEdit>style="display: none"</#if>>
          <span class="glyphicon glyphicon-remove"></span>
        </a>
      </div>
    </#if>
    <div class="panel-body">
      <table class="table table-condensed">
        <thead>
          <tr>
            <th>Input</th>
            <th <#if !hasOutput>style="display: none"</#if>>Output</th>
            <#if canEdit><th></th></#if>
          </tr>
        </thead>
        <tbody>
          <@testCase isTemplate=true hasOutput=hasOutput/>

          <#list 0..<inputFiles?size as i>
            <@testCase inputFile=inputFiles[i] outputFile=outputFiles[i] hasOutput=hasOutput/>
          </#list>

          <@addTestCaseForm hasOutput=hasOutput/>
        </tbody>
      </table>
      <#nested>
    </div>
  </div>
</#macro>

<#macro sampleTestData hasOutput=true>
  <#local inputFiles = splitCsv(form.testCaseInputs[0])>
  <#local outputFiles = splitCsv(form.testCaseOutputs[0])>

  <div class="row" id="sample-test-data">
    <div class="col-md-3">
      <label class="control-label">Sample test data</label>
    </div>

    <div class="col-md-9">
      <div class="panel panel-default">
        <div class="panel-body">
          <table class="table table-condensed">
            <thead>
              <tr>
                <th>Sample input</th>
                <th <#if !hasOutput>style="display: none"</#if>>Sample output</th>
                <#if canEdit><th></th></#if>
              </tr>
            </thead>
            <tbody>
              <@testCase isTemplate=true hasOutput=hasOutput/>

              <#list 0..<inputFiles?size as i>
                <@testCase inputFile=inputFiles[i] outputFile=outputFiles[i] hasOutput=hasOutput/>
              </#list>

              <@addTestCaseForm hasOutput=hasOutput/>
            </tbody>
          </table>
          <input type="hidden" name="testCaseInputs" value="${form.testCaseInputs[0]}">
          <input type="hidden" name="testCaseOutputs" value="${form.testCaseOutputs[0]}">
        </div>
      </div>
    </div>
  </div>
</#macro>

<#macro sampleTestDataWithSubtasks hasOutput=true>
  <#local inputFiles = splitCsv(form.testCaseInputs[0])>
  <#local outputFiles = splitCsv(form.testCaseOutputs[0])>

  <div class="row" id="sample-test-data">
    <div class="col-md-3">
      <label class="control-label">Sample test data</label>
    </div>

    <div class="col-md-9">
      <div class="panel panel-default">
        <div class="panel-body">
          <table class="table table-condensed" style="margin-bottom: 0">
            <thead>
              <tr>
                <th>Sample input</th>
                <th <#if !hasOutput>style="display: none"</#if>>Sample output</th>
                <#if canEdit><th></th></#if>
              </tr>
            </thead>
            <tbody>
              <@testCase isTemplate=true hasOutput=hasOutput/>
              <@sampleTestCaseSubtaskAssignment isTemplate=true>
                <input type="hidden" name="sampleTestCaseSubtaskIds" disabled>
              </@sampleTestCaseSubtaskAssignment>

              <#list 0..<inputFiles?size as i>
                <@testCase inputFile=inputFiles[i] outputFile=outputFiles[i] hasOutput=hasOutput/>
                <@sampleTestCaseSubtaskAssignment subtaskIds=splitCsv(form.sampleTestCaseSubtaskIds[i])>
                  <input type="hidden" name="sampleTestCaseSubtaskIds" value="${form.sampleTestCaseSubtaskIds[i]}">
                </@sampleTestCaseSubtaskAssignment>
              </#list>

              <@addTestCaseForm hasOutput=hasOutput/>
            </tbody>
          </table>
          <input type="hidden" name="testCaseInputs" value="${form.testCaseInputs[0]}">
          <input type="hidden" name="testCaseOutputs" value="${form.testCaseOutputs[0]}">
        </div>
      </div>
    </div>
  </div>
</#macro>

<#macro testData hasOutput=true>
  <#local inputFiles = splitCsv(form.testCaseInputs[1])>
  <#local outputFiles = splitCsv(form.testCaseOutputs[1])>

  <div class="row" id="test-data">
    <div class="col-md-3">
      <label class="control-label">Test data</label>
    </div>

    <div class="col-md-9">
      <@testGroup inputFiles=inputFiles outputFiles=outputFiles hasOutput=hasOutput>
        <input type="hidden" name="testCaseInputs" value="${form.testCaseInputs[1]}">
        <input type="hidden" name="testCaseOutputs" value="${form.testCaseOutputs[1]}">
      </@testGroup>
    </div>
  </div>
</#macro>

<#macro testDataWithSubtasks hasOutput=true>
  <div class="row">
    <div class="col-md-3">
      <label class="control-label">Test data</label>
    </div>

    <div class="col-md-9" id="test-groups">
      <@testGroup isTemplate=true heading="Test group" hasOutput=hasOutput>
        <input type="hidden" name="testCaseInputs" disabled>
        <input type="hidden" name="testCaseOutputs" disabled>
        <@testGroupSubtaskAssignment>
          <input type="hidden" name="testGroupSubtaskIds" disabled>
        </@testGroupSubtaskAssignment>
      </@testGroup>

      <#list 1..<form.testCaseInputs?size as i>
        <#local inputFiles = splitCsv(form.testCaseInputs[i])>
        <#local outputFiles = splitCsv(form.testCaseOutputs[i])>

        <@testGroup heading="Test group ${i}" hasOutput=hasOutput inputFiles=inputFiles outputFiles=outputFiles>
          <input type="hidden" name="testCaseInputs" value="${form.testCaseInputs[i]}">
          <input type="hidden" name="testCaseOutputs" value="${form.testCaseOutputs[i]}">
          <@testGroupSubtaskAssignment subtaskIds=splitCsv(form.testGroupSubtaskIds[i-1])>
            <input type="hidden" name="testGroupSubtaskIds" value="${form.testGroupSubtaskIds[i-1]}">
          </@testGroupSubtaskAssignment>
        </@testGroup>
      </#list>

      <#if canEdit>
        <button class="btn btn-primary btn-xs test-group-add-button" type="button">
          <span class="glyphicon glyphicon-plus"></span> New test group
        </button>
      </#if>
    </div>
  </div>
</#macro>

<#macro subtasks>
  <div class="row">
    <div class="col-md-3">
      <label class="control-label">Subtasks</label>
    </div>

    <div class="col-md-9">
      <div class="panel panel-default">
        <div class="panel-body">
          <table class="table table-condensed">
            <thead>
              <tr>
                <th>No</th>
                <th>Points</th>
              </tr>
            </thead>
            <tbody>
              <#list 0..<form.subtaskPoints?size as i>
                <tr>
                  <td>${i+1}</td>
                  <td><input type="text" name="subtaskPoints" class="form-control" value="${form.subtaskPoints[i]!""}" <#if !canEdit>disabled</#if>></td>
                </tr>
              </#list>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</#macro>

<#macro customScorer>
  <@forms.select form=form name="customScorer" label="Custom scorer" options=helperFilenamesForCustomScorer disabled=!canEdit/>
</#macro>

<#macro communicator>
  <@forms.select form=form name="communicator" label="Communicator" options=helperFilenamesForCommunicator disabled=!canEdit/>
</#macro>

<#macro autoPopulateByFilename>
  <#if !canEdit><#return></#if>
  <div class="row">
    <div class="col-md-offset-3 col-md-9">
      <a href="config/auto-populate" class="btn btn-primary btn-sm">
        Auto-populate test data based on filenames
      </a>
    </div>
  </div>
  <br>
</#macro>

<#macro autoPopulateByTCFrameFormat>
  <#if !canEdit><#return></#if>
  <div class="row">
    <div class="col-md-offset-3 col-md-9">
      <a href="config/auto-populate" class="btn btn-primary btn-sm">
        Auto-populate test data based on tcframe format
      </a>
    </div>
  </div>
  <br>
</#macro>

<#macro submit>
  <#if !canEdit><#return></#if>
  <@forms.submit>Update</@forms.submit>
</#macro>
