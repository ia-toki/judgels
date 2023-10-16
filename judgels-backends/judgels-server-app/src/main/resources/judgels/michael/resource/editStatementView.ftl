<#-- @ftlvariable type="judgels.michael.resource.EditStatementView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/forms.ftl" as forms>
<#import "switchLanguageView.ftl" as switchLanguage>

<@template.layout>
  <#include "ckeditor.ftl">
  <@switchLanguage.view languages=enabledLanguages language=language/>

  <@forms.form type="vertical" acceptCharset="utf-8">
    <#if formValues.title?has_content>
      <@forms.input name="title" label="Title" required=true disabled=!canEdit/>
    </#if>
    <@forms.textarea name="text" label="Text" class="ckeditor">
      <#include "statementManualButtons.html">
    </@forms.textarea>
    <#if canEdit>
      <@forms.submit>Update</@forms.submit>
    </#if>
  </@forms.form>
  <#include "katex.ftl">
  <#include "statementManualBody.html">
</@template.layout>
