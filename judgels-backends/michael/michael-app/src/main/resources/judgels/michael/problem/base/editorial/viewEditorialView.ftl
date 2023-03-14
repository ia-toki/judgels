<#-- @ftlvariable type="judgels.michael.problem.base.editorial.ViewEditorialView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/resource/switchLanguageView.ftl" as switchLanguage>

<@template.layout>
  <#include "/judgels/michael/resource/katex.ftl">
  <@switchLanguage.view languages=enabledLanguages language=language/>

  <div class="content-text">
    ${editorial.text?no_esc}
  </div>
</@template.layout>
