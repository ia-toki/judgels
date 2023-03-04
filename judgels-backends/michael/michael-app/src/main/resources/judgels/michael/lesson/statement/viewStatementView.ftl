<#-- @ftlvariable type="judgels.michael.problem.programming.statement.ViewStatementView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>

<#import "/judgels/michael/resource/statement/statementLanguageSelectionView.ftl" as statementLanguageSelection>

<@template.layout>
  <@statementLanguageSelection.view languages=enabledLanguages language=language/>

  <h2 class="text-center">${statement.title}</h2>

  <p>&nbsp;</p>

  <div class="content-text">
    ${statement.text?no_esc}
  </div>
</@template.layout>
