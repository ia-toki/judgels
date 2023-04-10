<#-- @ftlvariable type="judgels.michael.problem.bundle.item.EditItemView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/forms.ftl" as forms>
<#import "/judgels/michael/resource/switchLanguageView.ftl" as switchLanguage>

<#import "MultipleChoice.ftl" as multipleChoice>
<#import "ShortAnswer.ftl" as shortAnswer>
<#import "Essay.ftl" as essay>

<@template.layout>
  <#include "/judgels/michael/resource/ckeditor.ftl">
  <@switchLanguage.view languages=enabledLanguages language=language/>

  <h3>${itemTypeName} Item <#if item.number.isPresent()>(No. ${item.number.get()})</#if></h3>
  <@forms.form labelWidth=2 fieldWidth=10>
    <@forms.input name="meta" label="Internal note" disabled=!canEdit/>

    <@forms.textarea name="statement" label="Statement" class="ckeditor">
      <#include "/judgels/michael/resource/statementManualButtons.html">
    </@forms.textarea>

    <#if itemType == "MULTIPLE_CHOICE">
      <@multipleChoice.edit/>
    <#elseif itemType == "SHORT_ANSWER">
      <@shortAnswer.edit/>
    <#elseif itemType == "ESSAY">
      <@essay.edit/>
    </#if>

    <#if canEdit>
      <@forms.submit>Update</@forms.submit>
    </#if>
  </@forms.form>
  <#include "/judgels/michael/resource/katex.ftl">
  <#include "/judgels/michael/resource/statementManualBody.html">
</@template.layout>
