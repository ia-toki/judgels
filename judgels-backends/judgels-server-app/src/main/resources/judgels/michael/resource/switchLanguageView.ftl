<#import "/judgels/michael/forms.ftl" as forms>
<#import "/judgels/michael/ui.ftl" as ui>

<#macro view languages language>
  <div class="small pull-left">
    Current language: <span class="label label-default">${languages[language]}</span>
  </div>

  <div class="pull-right">
    <@forms.form type="inline" action="/switchLanguage">
      <@forms.formLabel for="language" value="Switch to"/>
      <@forms.formGroup>
        <select id="language" name="language">
          <#list languages as k, v>
            <option value="${k}" ${(k == language)?then("selected", "")}>${v}</option>
          </#list>
        </select>
      </@forms.formGroup>
      <@ui.button type="submit" size="xs">Switch</@ui.button>
    </@forms.form>
  </div>

  <div class="clearfix"></div>

  <hr>
</#macro>
