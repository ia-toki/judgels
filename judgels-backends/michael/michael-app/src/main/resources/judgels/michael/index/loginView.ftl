<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/template/form/horizontalForms.ftl" as forms>

<@template.layout>
  <div class="row login-content">
    <div class="col-md-12">
      <@forms.form>
        <@forms.text form=form name="username" label="Username" required=true/>
        <@forms.password name="password" label="Password" required=true/>
        <@forms.submit>Log in</@forms.submit>
      </@forms.form>
    </div>
  </div>
</@template.layout>
