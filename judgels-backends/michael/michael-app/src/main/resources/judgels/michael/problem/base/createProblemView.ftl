<#-- @ftlvariable type="judgels.michael.problem.base.CreateProblemView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>

<#import "/judgels/michael/template/form/forms.ftl" as forms>

<@template.layout>
  <@forms.form>
    <@forms.text name="slug" label="Slug" value=form.slug required=true pattern="[a-z0-9]+(-[a-z0-9]+)*" title="Slug can only consist of alphanumerics and dashes"/>
    <@forms.select name="gradingEngine" label="Grading engine" options=gradingEngines value="Batch"/>
    <@forms.textarea name="additionalNote" label="Additional note" value=form.additionalNote/>
    <@forms.select name="initialLanguage" label="Initial language" options=languages value="en-US"/>
    <@forms.submit>Create</@forms.submit>
  </@forms.form>
</@template.layout>
