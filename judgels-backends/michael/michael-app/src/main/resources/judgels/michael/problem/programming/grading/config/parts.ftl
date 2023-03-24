<#import "/judgels/michael/template/form/horizontalForms.ftl" as forms>

<#macro limits>
  <@forms.text form=form name="timeLimit" label="Time limit" addon="milliseconds" disabled=!canEdit/>
  <@forms.text form=form name="memoryLimit" label="Memory limit" addon="kilobytes" disabled=!canEdit/>
</#macro>

<#macro testCase isTemplate=false inputVal="" outputVal="">
  <tr <#if isTemplate>style="display: none"<#else>class="tc"</#if>>
    <td>
      <input class="form-control input-sm tc-input" type="text" disabled <#if inputVal?has_content>value="${inputVal}"</#if>>
    </td>
    <td>
      <input class="form-control input-sm tc-output" type="text" disabled <#if outputVal?has_content>value="${outputVal}"</#if>>
    </td>
    <td class="text-center">
      <a href="#" class="tc-remove-button" <#if !canEdit>style="display: none"</#if>>
        <span class="glyphicon glyphicon-remove"></span>
      </a>
    </td>
  </tr>
</#macro>

<#macro addTestCaseForm>
  <tr class="active tc-add-form" <#if !canEdit>style="display: none"</#if>>
    <td>
      <select class="tc-input">
        <#list testDataFiles as file>
          <option value="${file.name}">${file.name}</option>
        </#list>
      </select>
    </td>
    <td>
      <select class="tc-output">
        <#list testDataFiles as file>
          <option value="${file.name}">${file.name}</option>
        </#list>
      </select>
    </td>
    <td class="text-center">
      <a href="" class="tc-add-button"><span class="glyphicon glyphicon-plus"></span></a>
    </td>
  </tr>
</#macro>

<#macro sampleTestData>
  <#local inputs = form.testCaseInputs[0]?has_content?then(form.testCaseInputs[0]?split(","), [])>
  <#local outputs = form.testCaseOutputs[0]?has_content?then(form.testCaseOutputs[0]?split(","), [])>

  <div class="row" id="sample-test-data">
    <input type="hidden" name="testCaseInputs" value="${form.testCaseInputs[0]}">
    <input type="hidden" name="testCaseOutputs" value="${form.testCaseOutputs[0]}">

    <div class="col-md-3">
      <label class="control-label">Sample test data</label>
    </div>

    <div class="col-md-9">
      <div class="panel panel-default">
        <div class="panel-body">
          <table class="table table-condensed">
            <thead>
              <tr>
                <th>Sample Input</th>
                <th>Sample Output</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              <@testCase isTemplate=true/>

              <#list 0..<inputs?size as i>
                <@testCase inputVal=inputs[i] outputVal=outputs[i]/>
              </#list>

              <@addTestCaseForm/>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</#macro>

<#macro testData>
  <#local inputs = form.testCaseInputs[1]?has_content?then(form.testCaseInputs[1]?split(","), [])>
  <#local outputs = form.testCaseOutputs[1]?has_content?then(form.testCaseOutputs[1]?split(","), [])>

  <div class="row" id="test-data">
    <input type="hidden" name="testCaseInputs" value="${form.testCaseInputs[1]}">
    <input type="hidden" name="testCaseOutputs" value="${form.testCaseOutputs[1]}">

    <div class="col-md-3">
      <label class="control-label">Test data</label>
    </div>

    <div class="col-md-9">
      <div class="panel panel-default">
        <div class="panel-body">
          <table class="table table-condensed">
            <thead>
              <tr>
                <th>Input</th>
                <th>Output</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              <@testCase isTemplate=true/>

              <#list 0..<inputs?size as i>
                <@testCase inputVal=inputs[i] outputVal=outputs[i]/>
              </#list>

              <@addTestCaseForm/>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</#macro>

<#macro customScorer>
  <@forms.select form=form name="customScorer" label="Custom scorer" options=helperFilenames disabled=!canEdit/>
</#macro>

<#macro submit>
  <#if !canEdit><#return></#if>
  <@forms.submit>Update</@forms.submit>
</#macro>
