<#-- @ftlvariable type="judgels.michael.problem.editorial.NewEditorialView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/forms.ftl" as forms>

<@template.layout>
  <#if canEdit>
    <h3>New editorial</h3>
    <@forms.form>
      <@forms.select name="initialLanguage" label="Initial language" options=languages/>
      <@forms.submit>Create</@forms.submit>
    </@forms.form>
  <#else>
    <p>No editorials.</p>
  </#if>
</@template.layout>
