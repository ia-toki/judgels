<#-- @ftlvariable type="judgels.michael.account.role.EditRolesView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/ui.ftl" as ui>
<#import "/judgels/michael/forms.ftl" as forms>

<@template.layout>
  <p>User roles can be edited via a CSV form below.</p>

  <hr>

  <p><b>Row format:</b></p>

  <pre>
&lt;username&gt;,&lt;jophiel role&gt;,&lt;sandalphon role&gt;,&lt;uriel role&gt;,&lt;jerahmeel role&gt;
</pre>

  <ul style="margin-left: 25px">
    <li><code>&lt;jophiel role&gt;</code>: user management role</li>
    <li><code>&lt;sandalphon role&gt;</code>: problem/lesson management role</li>
    <li><code>&lt;uriel role&gt;</code>: contest management role</li>
    <li><code>&lt;jerahmeel role&gt;</code>: training management role</li>
  </ul>

  <p>Each role is either <code>ADMIN</code>, or an empty string.</p>

  <hr>

  <p>CSV rows, one user per row:</p>

  <@forms.form type="vertical">
    <@forms.csv name="csv"/>
    <@forms.submit>Submit</@forms.submit>
  </@forms.form>

  <hr>

  <p><b>Example</b></p>
  <pre>
andi,ADMIN,ADMIN,ADMIN,ADMIN
budi,,ADMIN,,</pre>
</@template.layout>
