<#-- @ftlvariable type="judgels.michael.resource.EditPartnersView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/forms.ftl" as forms>

<@template.layout>
  <p>Partners can be edited via a CSV form below.</p>

  <hr>

  <p><b>CSV row format</b></p>

  <p><p><code>username[,permission]</code></p>

  <p>where <code>permission</code> is either:</p>

  <ul style="margin-left: 25px">
    <li><code>UPDATE</code> (default if not provided): the user can view and update</li>
    <li><code>VIEW</code>: the user can only view</li>
  </ul>

  <hr>

  <p>CSV rows, one user per row, max 100 users:</p>
  <@forms.form type="vertical">
    <@forms.csv name="csv"/>
    <@forms.submit>Update</@forms.submit>
  </@forms.form>

  <hr>

  <p><b>Example</b></p>
  <pre>
andi,VIEW
budi
caca,UPDATE</pre>
</@template.layout>
