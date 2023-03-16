<#-- @ftlvariable type="judgels.michael.resource.EditPartnersView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/template/form/verticalForms.ftl" as forms>

<@template.layout>
  <h3>Edit partners</h3>
  <p><b>CSV format:</b> <code>username[,permission]</code>
  <br>
  where <code>permission</code> is either <code>UPDATE</code> (default), or <code>VIEW</code></p>
  <p><b>Example:</b></p>
  <pre>
andi,VIEW
budi
caca,UPDATE</pre>
  <hr>
  <p>One user per line, max 100 users:</p>
  <@forms.form>
    <@forms.textarea form=form name="csv"/>
    <@forms.submit>Update</@forms.submit>
  </@forms.form>
</@template.layout>
