<#-- @ftlvariable type="judgels.michael.resource.EditStatementView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/template/form/verticalForms.ftl" as forms>
<#import "switchLanguageView.ftl" as switchLanguage>

<@template.layout>
  <#include "ckeditor.ftl">
  <@switchLanguage.view baseUrl=baseUrl languages=enabledLanguages language=language/>

  <@forms.form>
    <@forms.text form=form name="title" label="Title" required=true/>
    <label class="control-label">Text</label>
    <#include "statementManualButtons.html">
    <div></div>
    <div class="form-group">
      <textarea class="ckeditor" name="text">
        ${form.text}
      </textarea>
    </div>
    <@forms.submit>Update</@forms.submit>
  </@forms.form>
  <#include "katex.ftl">
  <#include "statementManualBody.html">
</@template.layout>
