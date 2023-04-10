<#-- @ftlvariable type="judgels.michael.problem.programming.statement.ViewStatementView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/resource/switchLanguageView.ftl" as switchLanguage>
<#import "/judgels/michael/forms.ftl" as forms>

<@template.layout>
  <#include "/judgels/michael/resource/katex.ftl">
  <@switchLanguage.view languages=enabledLanguages language=language/>

  <#if reasonNotAllowedToSubmit?has_content>
    <div class="alert alert-danger">${reasonNotAllowedToSubmit}</div>
  </#if>

  <h2 class="text-center">${statement.title}</h2>

  <p class="text-center">
    Time limit:
    <#if (gradingConfig.timeLimit % 1000 == 0)>
      ${gradingConfig.timeLimit / 1000} s
    <#else>
      ${gradingConfig.timeLimit} ms
    </#if>
  </p>
  <p class="text-center">
    Memory limit:
    <#if (gradingConfig.memoryLimit % 1024 == 0)>
      ${gradingConfig.memoryLimit / 1024} MB
    <#else>
      ${gradingConfig.memoryLimit} KB
    </#if>
  </p>

  <p>&nbsp;</p>

  <div class="content-text">
    ${statement.text?no_esc}
  </div>

  <#if canSubmit>
    <hr>
    <h4>Submit solution</h4>

    <@forms.form multipart=true compact=true action="submissions">
      <input type="hidden" name="gradingEngine" value="${gradingEngine}">
      <input type="hidden" name="sourceKeys" value="${sourceKeys}">

      <#list gradingConfig.sourceFileFields as k, v>
        <@forms.file name="sourceFiles."+k label=v required=true/>
      </#list>

      <#if isOutputOnly>
        <input type="hidden" name="gradingLanguage" value="${outputOnlyGradingLanguage}">
      <#else>
        <@forms.select name="gradingLanguage" label="Language" options=allowedGradingLanguages/>
      </#if>

      <@forms.submit>Submit</@forms.submit>
    </@forms.form>
  </#if>
</@template.layout>
