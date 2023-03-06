<#-- @ftlvariable type="judgels.michael.lesson.statement.ViewStatementView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/resource/katexView.ftl" as katex>
<#import "/judgels/michael/resource/switchLanguageView.ftl" as switchLanguage>

<@template.layout>
  <@katex.view/>
  <@switchLanguage.view languages=enabledLanguages language=language/>

  <h2 class="text-center">${statement.title}</h2>

  <p>&nbsp;</p>

  <div class="content-text">
    ${statement.text?no_esc}
  </div>
</@template.layout>
