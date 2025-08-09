<#-- @ftlvariable type="judgels.michael.problem.NewProblemView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/forms.ftl" as forms>

<@template.layout>
  <@forms.form>
    <@forms.input name="slug" label="Slug" required=true autofocus=true pattern="[a-z0-9]+(-[a-z0-9]+)*" title="Slug can only consist of alphanumerics and dashes"/>
    <div class="form-group">
      <div class="col-md-3"></div>
      <div class="col-md-9">
        <#include "problemSlugGuide.html">
      </div>
    </div>
    <@forms.select name="gradingEngine" label="Grading engine" options=gradingEngines/>
    <@forms.textarea name="additionalNote" label="Additional note"/>
    <@forms.select name="initialLanguage" label="Initial language" options=languages/>
    <@forms.submit>Create</@forms.submit>
  </@forms.form>
</@template.layout>
