<#-- @ftlvariable type="judgels.michael.problem.bundle.statement.ViewStatementView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/resource/switchLanguageView.ftl" as switchLanguage>
<#import "/judgels/michael/problem/bundle/item/viewItemView.ftl" as viewItem>
<#import "/judgels/michael/forms.ftl" as forms>
<#import "/judgels/michael/ui.ftl" as ui>

<@template.layout>
  <#include "/judgels/michael/resource/katex.ftl">
  <@switchLanguage.view languages=enabledLanguages language=language/>

  <#if reasonNotAllowedToSubmit?has_content>
    <div class="alert alert-danger">${reasonNotAllowedToSubmit}</div>
  </#if>

  <h2 class="text-center">${statement.title}</h2>

  <p>&nbsp;</p>

  <div class="content-text">
    ${statement.text?no_esc}
  </div>

  <hr>

  <@forms.form type="vertical" action="submissions">
    <#list 0..<items?size as i>
      <@viewItem.view item=items[i] type=itemTypes[i] config=itemConfigs[i]/>
    </#list>

    <#if canSubmit && !reasonNotAllowedToSubmit?has_content>
      <@forms.submit>Submit</@forms.submit>
    </#if>
  </@forms.form>

  <script>
    <#include "/judgels/michael/problem/bundle/item/MultipleChoiceRadioButton.js">
  </script>
</@template.layout>
