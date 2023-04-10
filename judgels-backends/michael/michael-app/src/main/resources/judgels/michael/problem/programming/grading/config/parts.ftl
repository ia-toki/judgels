<#import "/judgels/michael/forms.ftl" as forms>
<#import "/judgels/michael/ui.ftl" as ui>

<#macro limits>
  <@forms.input type="number" name="timeLimit" label="Time limit" addon="milliseconds" required=true disabled=!canEdit/>
  <@forms.input type="number" name="memoryLimit" label="Memory limit" addon="kilobytes" required=true disabled=!canEdit/>
</#macro>

<#macro keys>
  <@forms.input name="sourceFileFieldKeys" label="Keys" addon="comma-separated keys" required=true disabled=!canEdit/>
</#macro>

<#macro testCase isTemplate=false inputFile="" outputFile="" hasOutput=true>
  <tr class="test-case <#if isTemplate>test-case-template hidden</#if>">
    <td>
      <input class="form-control input-sm test-case-input" type="text" disabled value="${inputFile}">
    </td>
    <td <#if !hasOutput>class="hidden"</#if>>
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
      <td class="hidden">
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
  <tr class="subtask-assignment <#if isTemplate>subtask-assignment-template hidden</#if>">
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
  <div class="panel panel-default test-group <#if isTemplate>test-group-template hidden</#if>">
    <#if heading?has_content>
      <div class="panel-heading">
        <span>${heading}</span>
        <a href="#" class="test-group-remove-button <#if !canEdit>hidden</#if>">
          <span class="glyphicon glyphicon-remove"></span>
        </a>
      </div>
    </#if>
    <div class="panel-body">
      <table class="table table-condensed">
        <thead>
          <tr>
            <th>Input</th>
            <th <#if !hasOutput>class="hidden"</#if>>Output</th>
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
  <#local inputFiles = splitCsv(formValues.testCaseInputs[0])>
  <#local outputFiles = splitCsv(formValues.testCaseOutputs[0])>

  <div class="row" id="sample-test-data">
    <@forms.formLabel value="Sample test data"/>
    <@forms.formField>
      <div class="panel panel-default">
        <div class="panel-body">
          <table class="table table-condensed">
            <thead>
              <tr>
                <th>Sample input</th>
                <th <#if !hasOutput>class="hidden"</#if>>Sample output</th>
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
          <input type="hidden" name="testCaseInputs" value="${formValues.testCaseInputs[0]}">
          <input type="hidden" name="testCaseOutputs" value="${formValues.testCaseOutputs[0]}">
        </div>
      </div>
    </@forms.formField>
  </div>
</#macro>

<#macro sampleTestDataWithSubtasks hasOutput=true>
  <#local inputFiles = splitCsv(formValues.testCaseInputs[0])>
  <#local outputFiles = splitCsv(formValues.testCaseOutputs[0])>

  <div class="row" id="sample-test-data">
    <@forms.formLabel value="Sample test data"/>

    <@forms.formField>
      <div class="panel panel-default">
        <div class="panel-body">
          <table class="table table-condensed" style="margin-bottom: 0">
            <thead>
              <tr>
                <th>Sample input</th>
                <th <#if !hasOutput>class="hidden"</#if>>Sample output</th>
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
                <@sampleTestCaseSubtaskAssignment subtaskIds=splitCsv(formValues.sampleTestCaseSubtaskIds[i])>
                  <input type="hidden" name="sampleTestCaseSubtaskIds" value="${formValues.sampleTestCaseSubtaskIds[i]}">
                </@sampleTestCaseSubtaskAssignment>
              </#list>

              <@addTestCaseForm hasOutput=hasOutput/>
            </tbody>
          </table>
          <input type="hidden" name="testCaseInputs" value="${formValues.testCaseInputs[0]}">
          <input type="hidden" name="testCaseOutputs" value="${formValues.testCaseOutputs[0]}">
        </div>
      </div>
    </@forms.formField>
  </div>
</#macro>

<#macro testData hasOutput=true>
  <#local inputFiles = splitCsv(formValues.testCaseInputs[1])>
  <#local outputFiles = splitCsv(formValues.testCaseOutputs[1])>

  <div class="row" id="test-data">
    <@forms.formLabel value="Test data"/>
    <@forms.formField>
      <@testGroup inputFiles=inputFiles outputFiles=outputFiles hasOutput=hasOutput>
        <input type="hidden" name="testCaseInputs" value="${formValues.testCaseInputs[1]}">
        <input type="hidden" name="testCaseOutputs" value="${formValues.testCaseOutputs[1]}">
      </@testGroup>
    </@forms.formField>
  </div>
</#macro>

<#macro testDataWithSubtasks hasOutput=true>
  <div class="row">
    <@forms.formLabel value="Test data"/>
    <@forms.formField id="test-groups">
      <@testGroup isTemplate=true heading="Test group" hasOutput=hasOutput>
        <input type="hidden" name="testCaseInputs" disabled>
        <input type="hidden" name="testCaseOutputs" disabled>
        <@testGroupSubtaskAssignment>
          <input type="hidden" name="testGroupSubtaskIds" disabled>
        </@testGroupSubtaskAssignment>
      </@testGroup>

      <#list 1..<formValues.testCaseInputs?size as i>
        <#local inputFiles = splitCsv(formValues.testCaseInputs[i])>
        <#local outputFiles = splitCsv(formValues.testCaseOutputs[i])>

        <@testGroup heading="Test group ${i}" hasOutput=hasOutput inputFiles=inputFiles outputFiles=outputFiles>
          <input type="hidden" name="testCaseInputs" value="${formValues.testCaseInputs[i]}">
          <input type="hidden" name="testCaseOutputs" value="${formValues.testCaseOutputs[i]}">
          <@testGroupSubtaskAssignment subtaskIds=splitCsv(formValues.testGroupSubtaskIds[i-1])>
            <input type="hidden" name="testGroupSubtaskIds" value="${formValues.testGroupSubtaskIds[i-1]}">
          </@testGroupSubtaskAssignment>
        </@testGroup>
      </#list>

      <#if canEdit>
        <@ui.button size="xs" class="test-group-add-button">
          <span class="glyphicon glyphicon-plus"></span> New test group
        </@ui.button>
      </#if>
    </@forms.formField>
  </div>
</#macro>

<#macro subtasks>
  <div class="row">
    <@forms.formLabel value="Subtasks"/>
    <@forms.formField>
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
              <#list 0..<formValues.subtaskPoints?size as i>
                <tr>
                  <td>${i+1}</td>
                  <td><input type="number" name="subtaskPoints" class="form-control" value="${formValues.subtaskPoints[i]!""}" <#if !canEdit>disabled</#if>></td>
                </tr>
              </#list>
            </tbody>
          </table>
        </div>
      </div>
    </@forms.formField>
  </div>
</#macro>

<#macro customScorer>
  <@forms.select name="customScorer" label="Custom scorer" options=helperFilenamesForCustomScorer disabled=!canEdit/>
</#macro>

<#macro communicator>
  <@forms.select name="communicator" label="Communicator" options=helperFilenamesForCommunicator disabled=!canEdit/>
</#macro>

<#macro autoPopulateByFilename>
  <#if !canEdit><#return></#if>
  <div class="row">
    <@forms.formField offset=true>
      <@ui.buttonLink size="sm" to="config/auto-populate">
        Auto-populate test data based on filenames
      </@ui.buttonLink>
    </@forms.formField>
  </div>
  <br>
</#macro>

<#macro autoPopulateByTCFrameFormat>
  <#if !canEdit><#return></#if>
  <div class="row">
    <@forms.formField offset=true>
      <@ui.buttonLink size="sm" to="config/auto-populate">
        Auto-populate test data based on tcframe format
      </@ui.buttonLink>
    </@forms.formField>
  </div>
  <br>
</#macro>

<#macro submit>
  <#if !canEdit><#return></#if>
  <@forms.submit>Update</@forms.submit>
</#macro>
