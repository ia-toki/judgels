<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/forms.ftl" as forms>

<@template.layout>
  <@forms.form>
    <@forms.input name="username" label="Username" required=true autofocus=true/>
    <@forms.password name="password" label="Password" required=true/>
    <@forms.submit>Log in</@forms.submit>
  </@forms.form>

  <#if hasGoogleAuth>
    <br>
    <br>
    <p><small>Registered via Google? Go to <strong>My account</strong> &rarr; <strong>Reset password</strong> to set your password.</small></p>
  </#if>
</@template.layout>
