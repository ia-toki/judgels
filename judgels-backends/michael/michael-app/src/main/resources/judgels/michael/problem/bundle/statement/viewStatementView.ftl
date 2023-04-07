<#-- @ftlvariable type="judgels.michael.problem.bundle.statement.ViewStatementView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/template/form/horizontalForms.ftl" as forms>
<#import "/judgels/michael/resource/switchLanguageView.ftl" as switchLanguage>
<#import "/judgels/michael/problem/bundle/item/viewItemView.ftl" as viewItem>

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

  <@forms.form action="submissions">
    <#list 0..<items?size as i>
      <@viewItem.view item=items[i] type=itemTypes[i] config=itemConfigs[i]/>
    </#list>

    <#if canSubmit && !reasonNotAllowedToSubmit?has_content>
      <button type="submit" class="btn btn-primary">Submit</button>
      <br>
      <br>
    </#if>
  </@forms.form>

  <script>
    <#include "/judgels/michael/problem/bundle/item/MultipleChoiceRadioButton.js">
  </script>
</@template.layout>
