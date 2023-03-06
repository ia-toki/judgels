<#-- @ftlvariable type="judgels.michael.problem.base.statement.EditStatementView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/template/form/verticalForms.ftl" as forms>
<#import "/judgels/michael/resource/ckeditorView.ftl" as ckeditor>
<#import "/judgels/michael/resource/switchLanguageView.ftl" as switchLanguage>

<@template.layout>
  <@ckeditor.view/>
  <@switchLanguage.view languages=enabledLanguages language=language/>

  <@forms.form>
    <@forms.text form=form name="title" label="Title" required=true/>
    <label class="control-label">Text</label>
    <div></div>
    <div class="form-group">
      <textarea class="ckeditor" name="text">
        ${form.text}
      </textarea>
    </div>
    <@forms.submit>Update</@forms.submit>
  </@forms.form>
</@template.layout>
