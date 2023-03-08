<#-- @ftlvariable type="judgels.michael.problem.programming.statement.ViewStatementView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/resource/switchLanguageView.ftl" as switchLanguage>

<@template.layout>
  <#include "/judgels/michael/resource/katex.ftl">
  <@switchLanguage.view baseUrl="/problems" languages=enabledLanguages language=language/>

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
</@template.layout>
