<#-- @ftlvariable type="judgels.michael.problem.programming.grading.config.BatchGradingConfigView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/template/form/horizontalForms.ftl" as forms>
<#import "parts.ftl" as parts>

<@template.layout>
  <@forms.form>
    <@parts.limits/>
    <@parts.sampleTestData/>
    <@parts.testData/>
    <@parts.customScorer/>
    <@parts.submit/>
  </@forms.form>

  <script>
    <#include "withoutSubtasks.js">
    addEventListeners();
  </script>
</@template.layout>
