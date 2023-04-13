<#-- @ftlvariable type="judgels.michael.problem.programming.grading.config.EditGradingConfigView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/forms.ftl" as forms>
<#import "parts.ftl" as parts>

<@template.layout>
  <@forms.form>
    <@parts.autoPopulateByFilename/>
    <@parts.limits/>
    <@parts.sampleTestData/>
    <@parts.testData/>
    <@parts.customScorer/>
    <@parts.submit/>
  </@forms.form>

  <#if canEdit>
    <script>
      <#include "config.js">
      configure();
    </script>
  </#if>
</@template.layout>
