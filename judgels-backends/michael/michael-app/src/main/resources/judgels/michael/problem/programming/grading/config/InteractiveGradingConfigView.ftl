<#-- @ftlvariable type="judgels.michael.problem.programming.grading.config.InteractiveGradingConfigView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/template/form/horizontalForms.ftl" as forms>
<#import "parts.ftl" as parts>

<@template.layout>
  <@forms.form>
    <@parts.limits/>
    <@parts.sampleTestData hasOutput=false/>
    <@parts.testData hasOutput=false/>
    <@parts.communicator/>
    <@parts.submit/>
  </@forms.form>

  <script>
    <#include "withoutSubtasks.js">
    addEventListeners();
  </script>
</@template.layout>
