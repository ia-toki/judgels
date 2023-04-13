<#-- @ftlvariable type="judgels.michael.problem.programming.grading.EditGradingEngineView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/forms.ftl" as forms>

<@template.layout>
  <@forms.form>
    <@forms.select
      name="gradingEngine"
      label="Grading engine"
      options=gradingEngines
      disabled=!canEdit
      help="Changing grading engine will reset grading config."/>
    <#if canEdit>
      <@forms.submit>Update</@forms.submit>
    </#if>
  </@forms.form>
</@template.layout>
