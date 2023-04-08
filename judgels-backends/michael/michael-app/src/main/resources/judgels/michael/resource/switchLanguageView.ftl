<#import "/judgels/michael/template/form/inlineForms.ftl" as forms>
<#import "/judgels/michael/template/ui/buttons.ftl" as buttons>

<#macro view languages language>
  <div class="small pull-left">
    Current language: <span class="label label-default">${languages[language]}</span>
  </div>

  <div class="pull-right">
    <@forms.form action="/switchLanguage">
      <label for="language"><span class="small">Switch to</span></label>
      <div class="form-group">
        <select id="language" name="language">
          <#list languages as k, v>
            <option value="${k}" ${(k == language)?then("selected", "")}>${v}</option>
          </#list>
        </select>
      </div>
      <@buttons.submit size="xs">Switch</@buttons.submit>
    </@forms.form>
  </div>

  <div class="clearfix"></div>

  <hr>
</#macro>
