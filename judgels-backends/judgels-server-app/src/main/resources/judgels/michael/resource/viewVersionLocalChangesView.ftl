<#-- @ftlvariable type="judgels.michael.resource.ViewVersionLocalChangesView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/forms.ftl" as forms>
<#import "/judgels/michael/ui.ftl" as ui>

<@template.layout>
  <#if isClean>
    <p>No local changes.</p>
  <#else>
    <h3>Commit local changes</h3>
    <#if formValues.localChangesError?has_content>
     <p>${formValues.localChangesError}</p>
    <#else>
      <@forms.form>
        <@forms.input name="title" label="Title" required=true autofocus=true/>
        <@forms.textarea name="description" label="Description"/>
        <@forms.submit>Commit</@forms.submit>
      </@forms.form>
    </#if>
    <hr>
    <h3>Modify local changes</h3>
    <@forms.formGroup>
      <@ui.buttonLink to="rebase">Rebase local changes on top of other users' changes</@ui.buttonLink>
      <@ui.buttonLink intent="danger" to="discard">Discard local changes</@ui.buttonLink>
    </@forms.formGroup>
  </#if>
</@template.layout>

