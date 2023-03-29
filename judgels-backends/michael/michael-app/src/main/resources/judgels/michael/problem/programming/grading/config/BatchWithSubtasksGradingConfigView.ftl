<#-- @ftlvariable type="judgels.michael.problem.programming.grading.config.EditGradingConfigView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/template/form/horizontalForms.ftl" as forms>
<#import "parts.ftl" as parts>

<@template.layout>
  <@forms.form>
    <@parts.limits/>
    <@parts.sampleTestDataWithSubtasks/>
    <@parts.testDataWithSubtasks/>
    <@parts.subtasks/>
    <@parts.customScorer/>
    <@parts.submit/>
  </@forms.form>

  <#if canEdit>
    <script>
      <#include "config.js">
      configureWithSubtasks();
    </script>
  </#if>
</@template.layout>
