<#-- @ftlvariable type="judgels.michael.resource.ViewVersionLocalChangesView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/template/form/horizontalForms.ftl" as forms>
<#import "/judgels/michael/ui.ftl" as ui>

<@template.layout>
  <#if isClean>
    <p>No local changes.</p>
  <#else>
    <h3>Commit local changes</h3>
    <#if form.localChangesError?has_content>
     <p>${form.localChangesError}</p>
    <#else>
      <@forms.form>
        <@forms.text form=form name="title" label="Title" required=true autofocus=true/>
        <@forms.textarea form=form name="description" label="Description"/>
        <@forms.submit>Commit</@forms.submit>
      </@forms.form>
    </#if>
    <hr>
    <h3>Modify local changes</h3>
    <div class="form-group">
      <@ui.buttonLink to="rebase">Rebase local changes on top of other users' changes</@ui.buttonLink>
      <@ui.buttonLink intent="danger" to="discard">Discard local changes</@ui.buttonLink>
    </div>
  </#if>
</@template.layout>

